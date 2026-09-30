package mk.ukim.finki.mcptoolbackend.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import mk.ukim.finki.mcptoolbackend.model.domain.DonationBatch;
import mk.ukim.finki.mcptoolbackend.model.domain.Resource;
import mk.ukim.finki.mcptoolbackend.model.enums.DonationStatus;

public record DisplayDonationBatchDto(
    Long id,
    DonationStatus status,
    String vezilkaReference,
    LocalDateTime submittedAt,
    Integer resourceCount,
    List<Long> resourceIds,
    LocalDateTime createdAt
) {
    public static DisplayDonationBatchDto from(DonationBatch batch) {
        List<Long> resourceIds = batch.getResources()
            .stream()
            .map(Resource::getId)
            .toList();
        return new DisplayDonationBatchDto(
            batch.getId(),
            batch.getStatus(),
            batch.getVezilkaReference(),
            batch.getSubmittedAt(),
            resourceIds.size(),
            resourceIds,
            batch.getCreatedAt()
        );
    }

    public static List<DisplayDonationBatchDto> from(List<DonationBatch> batches) {
        return batches
            .stream()
            .map(DisplayDonationBatchDto::from)
            .toList();
    }
}
