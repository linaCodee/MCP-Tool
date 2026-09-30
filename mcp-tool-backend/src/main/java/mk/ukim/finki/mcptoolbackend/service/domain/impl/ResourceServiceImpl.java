package mk.ukim.finki.mcptoolbackend.service.domain.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import mk.ukim.finki.mcptoolbackend.analysis.AnalysisOutcome;
import mk.ukim.finki.mcptoolbackend.analysis.LanguageDetector;
import mk.ukim.finki.mcptoolbackend.analysis.ResourceAnalyzer;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.domain.ResourceAnalysis;
import mk.ukim.finki.mcptoolbackend.model.dto.ResourceFilterDto;
import mk.ukim.finki.mcptoolbackend.model.exception.ResourceNotFoundException;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * TODO(student): Implement this service.
 *
 * <p>Build a {@code Specification<Resource>} from the non-null fields of the
 * {@link ResourceFilterDto} and call {@code resourceRepository.findAll(spec,
 * pageable)}. For {@code analyze}, delegate to {@link ResourceAnalyzer} and
 * {@link LanguageDetector}.</p>
 */
@Service
public class ResourceServiceImpl implements ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourceAnalyzer resourceAnalyzer;
    private final LanguageDetector languageDetector;

    public ResourceServiceImpl(ResourceRepository resourceRepository,
                               ResourceAnalyzer resourceAnalyzer,
                               LanguageDetector languageDetector) {
        this.resourceRepository = resourceRepository;
        this.resourceAnalyzer = resourceAnalyzer;
        this.languageDetector = languageDetector;
    }

    @Override
    public Page<Resource> findAll(ResourceFilterDto filter, Pageable pageable) {
        Specification<Resource> specification=Specification.where(null);

        if (filter.searchRunId()!=null){
            specification=specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("searchRun").get("id"),filter.searchRunId())));
        }

        if (filter.minMacedonianConfidence()!=null){
            specification=specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("macedonianConfidence"),filter.minMacedonianConfidence())));
        }

        if (filter.analyzed()!=null){
            if (filter.analyzed()){
                specification=specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("analysis"))));
            } else {
                specification=specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("analysis"))));
            }
        }

        if (filter.donated()!=null){
            if (filter.donated()){
                specification=specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("donationBatch"))));
            } else {
                specification=specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.isNull(root.get("donationBatch"))));
            }
        }

        if (filter.search()!=null && !filter.search().isBlank()){
            String sh="%"+filter.search().toLowerCase()+"%";
            specification=specification.and(((root, query, criteriaBuilder) -> criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")),sh),criteriaBuilder.like(criteriaBuilder.lower(root.get("content")),sh))));
        }

        return resourceRepository.findAll(specification,pageable);
    }

    @Override
    public Optional<Resource> findById(Long id) {
        return resourceRepository.findById(id);
    }

    @Override
    public Optional<Resource> deleteById(Long id) {
        Optional<Resource> resource=resourceRepository.findById(id);
        resource.ifPresent(resourceRepository::delete);
        return resource;
    }

    @Override
    public Resource analyze(Long id) {
        // TODO(student):
        //  1. Load the resource (or throw ResourceNotFoundException).
        //  2. AnalysisOutcome outcome = resourceAnalyzer.analyze(title, content).
        //  3. Create/update a ResourceAnalysis from the outcome; set its
        //     macedonianConfidence from languageDetector; set analyzedAt = now.
        //  4. Persist and return the resource.
        Resource resource=resourceRepository.findById(id).orElseThrow(()->new ResourceNotFoundException(id));

        AnalysisOutcome outcome=resourceAnalyzer.analyze(resource.getTitle(), resource.getContent());

        ResourceAnalysis resourceAnalysis=resource.getAnalysis();
        if (resourceAnalysis==null){
            resourceAnalysis=new ResourceAnalysis(resource);
            resource.setAnalysis(resourceAnalysis);
        }

        resourceAnalysis.setSummary(outcome.summary());
        resourceAnalysis.setKeywords(String.join(", ",outcome.keywords()));
        resourceAnalysis.setSentenceCount(outcome.sentenceCount());
        resourceAnalysis.setAnalyzedAt(LocalDateTime.now());
        resourceAnalysis.setMacedonianConfidence(languageDetector.macedonianConfidence(resource.getContent()));

        return resourceRepository.save(resource);
    }
}
