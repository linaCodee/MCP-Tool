package mk.ukim.finki.mcptoolbackend.service.domain.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.analysis.LanguageDetector;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.domain.SearchRun;
import mk.ukim.finki.mcptoolbackend.model.enums.SearchStatus;
import mk.ukim.finki.mcptoolbackend.repository.ResourceRepository;
import mk.ukim.finki.mcptoolbackend.repository.SearchRunRepository;
import mk.ukim.finki.mcptoolbackend.service.domain.SearchRunService;
import mk.ukim.finki.mcptoolbackend.source.FetchedResource;
import mk.ukim.finki.mcptoolbackend.source.SourceClient;
import org.springframework.stereotype.Service;

/**
 * TODO(student): Implement this service.
 *
 * <p>The dependencies you will need are already injected: {@link SourceClient}
 * to reach the assigned website, {@link LanguageDetector} to score Macedonian
 * content, and the repositories to persist the run and its resources.</p>
 */
@Service
public class SearchRunServiceImpl implements SearchRunService {
    private final SearchRunRepository searchRunRepository;
    private final ResourceRepository resourceRepository;
    private final SourceClient sourceClient;
    private final LanguageDetector languageDetector;

    public SearchRunServiceImpl(SearchRunRepository searchRunRepository,
                                ResourceRepository resourceRepository,
                                SourceClient sourceClient,
                                LanguageDetector languageDetector) {
        this.searchRunRepository = searchRunRepository;
        this.resourceRepository = resourceRepository;
        this.sourceClient = sourceClient;
        this.languageDetector = languageDetector;
    }

    @Override
    public List<SearchRun> findAll() {
        return searchRunRepository.findAll();
    }

    @Override
    public Optional<SearchRun> findById(Long id) {
        return searchRunRepository.findById(id);
    }

    @Override
    public SearchRun run(String query, int limit) {
        // TODO(student):
        //  1. Save a new SearchRun(query) with status RUNNING and startedAt = now.
        //  2. sourceClient.search(query, limit) -> for each FetchedResource, build a
        //     Resource, set fetchedAt, wordCount, and
        //     macedonianConfidence = languageDetector.macedonianConfidence(content).
        //  3. Persist the resources, set resultCount, mark the run COMPLETED
        //     (finishedAt = now). On any failure, mark it FAILED and rethrow.

        SearchRun searchRun=new SearchRun(query);

        searchRun.setStatus(SearchStatus.RUNNING);
        searchRun.setStartedAt(LocalDateTime.now());
        searchRun=searchRunRepository.save(searchRun);

        try {
            List<FetchedResource> fetchedResources=sourceClient.search(query,limit);
            for (FetchedResource fetchedResource:fetchedResources){
                Resource resource=new Resource(searchRun, fetchedResource.externalId(), fetchedResource.title(), fetchedResource.content(), fetchedResource.sourceUrl());

                resource.setFetchedAt(LocalDateTime.now());

                String content=fetchedResource.content();
                int wordCount=content==null || content.isBlank() ? 0 : content.split("\\s+").length;

                resource.setWordCount(wordCount);
                resource.setMacedonianConfidence(languageDetector.macedonianConfidence(content));

                resourceRepository.save(resource);
            }
            searchRun.setResultCount(fetchedResources.size());
            searchRun.setStatus(SearchStatus.COMPLETED);
            searchRun.setFinishedAt(LocalDateTime.now());
            return searchRunRepository.save(searchRun);
        } catch (RuntimeException e){
            searchRun.setStatus(SearchStatus.FAILED);
            searchRun.setFinishedAt(LocalDateTime.now());
            searchRunRepository.save(searchRun);

            throw e;
        }
    }

    @Override
    public List<Resource> findResources(Long searchRunId) {
        return resourceRepository.findAllBySearchRunId(searchRunId);
    }
}
