package mk.ukim.finki.mcptoolbackend.service.application.impl;

import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.model.dto.DisplayResourceDto;
import mk.ukim.finki.mcptoolbackend.model.dto.ResourceFilterDto;
import mk.ukim.finki.mcptoolbackend.service.application.ResourceApplicationService;
import mk.ukim.finki.mcptoolbackend.service.domain.ResourceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * TODO(student): Implement this service. Build a {@code Pageable} (e.g.
 * {@code PageRequest.of(page, size)}), delegate to {@link ResourceService},
 * and map with {@code DisplayResourceDto.from(...)} (use {@code Page.map}).
 */
@Service
public class ResourceApplicationServiceImpl implements ResourceApplicationService {
    private final ResourceService resourceService;

    public ResourceApplicationServiceImpl(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Override
    public Page<DisplayResourceDto> findAll(ResourceFilterDto filter, int page, int size) {
        Pageable pageable= PageRequest.of(page,size);
        return resourceService.findAll(filter,pageable).map(DisplayResourceDto::from);
    }

    @Override
    public Optional<DisplayResourceDto> findById(Long id) {
        return resourceService.findById(id).map(DisplayResourceDto::from);
    }

    @Override
    public Optional<DisplayResourceDto> deleteById(Long id) {
        return resourceService.deleteById(id).map(DisplayResourceDto::from);
    }

    @Override
    public Optional<DisplayResourceDto> analyze(Long id) {
        return Optional.of(DisplayResourceDto.from(resourceService.analyze(id)));
    }
}
