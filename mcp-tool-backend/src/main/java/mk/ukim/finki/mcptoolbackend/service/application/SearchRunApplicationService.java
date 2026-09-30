package mk.ukim.finki.mcptoolbackend.service.application;

import java.util.List;
import java.util.Optional;
import mk.ukim.finki.mcptoolbackend.model.dto.DisplayResourceDto;
import mk.ukim.finki.mcptoolbackend.model.dto.DisplaySearchRunDto;
import mk.ukim.finki.mcptoolbackend.model.dto.RunSearchRequestDto;

/**
 * TODO(student): Implement {@code SearchRunApplicationServiceImpl} — map
 * between DTOs and entities and delegate to {@code SearchRunService}.
 */
public interface SearchRunApplicationService {
    List<DisplaySearchRunDto> findAll();

    Optional<DisplaySearchRunDto> findById(Long id);

    Optional<DisplaySearchRunDto> run(RunSearchRequestDto request);

    List<DisplayResourceDto> findResources(Long searchRunId);
}
