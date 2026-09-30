package mk.ukim.finki.mcptoolbackend.mcp;

import mk.ukim.finki.mcptoolbackend.model.dto.CreateDonationBatchDto;
import mk.ukim.finki.mcptoolbackend.model.dto.DisplayDonationBatchDto;
import mk.ukim.finki.mcptoolbackend.model.exception.DonationBatchNotFoundException;
import mk.ukim.finki.mcptoolbackend.service.application.DonationApplicationService;
import mk.ukim.finki.mcptoolbackend.service.domain.ToolInvocationLogService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * MCP tool for donating a resource to doniraj.vezilka.ai.
 *
 * <p>TODO(student): Implement {@code donateResource}. Follow
 * {@link CorpusStatsTool}. Create a batch, approve and submit it through
 * {@link DonationApplicationService}, log the invocation, and return the batch.</p>
 */
@Service
public class DonationTool {
    private final DonationApplicationService donationApplicationService;
    private final ToolInvocationLogService toolInvocationLogService;

    public DonationTool(DonationApplicationService donationApplicationService,
                        ToolInvocationLogService toolInvocationLogService) {
        this.donationApplicationService = donationApplicationService;
        this.toolInvocationLogService = toolInvocationLogService;
    }

    @Tool(name = "donate_resource",
        description = "Donate a single stored resource to doniraj.vezilka.ai by creating a donation batch, "
            + "approving it and submitting it. Returns the resulting donation batch with its Vezilka reference.")
    public DisplayDonationBatchDto donateResource(
        @ToolParam(description = "The id of the stored resource to donate") Long id) {
        // TODO(student): create -> approve -> submit a batch for this resource via
        //  donationApplicationService, log the invocation, and return the DTO.
        CreateDonationBatchDto request=new CreateDonationBatchDto(List.of(id));

        DisplayDonationBatchDto created=donationApplicationService.create(request).orElseThrow();

        DisplayDonationBatchDto approved=donationApplicationService.approve(created.id()).orElseThrow(()->new DonationBatchNotFoundException(created.id()));

        DisplayDonationBatchDto submitted=donationApplicationService.submit(approved.id()).orElseThrow(()->new DonationBatchNotFoundException(approved.id()));

        toolInvocationLogService.log(
                "donate_resource",
                "id=%s".formatted(id),
                submitted.toString(),
                true,
                null
        );
        return submitted;
    }
}
