package mk.ukim.finki.mcptoolbackend.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import mk.ukim.finki.mcptoolbackend.model.domain.SearchRun;
import mk.ukim.finki.mcptoolbackend.model.enums.SearchStatus;

public record DisplaySearchRunDto(
    Long id,
    String query,
    SearchStatus status,
    LocalDateTime startedAt,
    LocalDateTime finishedAt,
    Integer resultCount,
    LocalDateTime createdAt
) {
    public static DisplaySearchRunDto from(SearchRun searchRun) {
        return new DisplaySearchRunDto(
            searchRun.getId(),
            searchRun.getQuery(),
            searchRun.getStatus(),
            searchRun.getStartedAt(),
            searchRun.getFinishedAt(),
            searchRun.getResultCount(),
            searchRun.getCreatedAt()
        );
    }

    public static List<DisplaySearchRunDto> from(List<SearchRun> searchRuns) {
        return searchRuns
            .stream()
            .map(DisplaySearchRunDto::from)
            .toList();
    }
}
