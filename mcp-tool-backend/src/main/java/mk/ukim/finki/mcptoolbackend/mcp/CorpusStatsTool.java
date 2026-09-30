package mk.ukim.finki.mcptoolbackend.mcp;

import mk.ukim.finki.mcptoolbackend.model.dto.DisplayCorpusStatsDto;
import mk.ukim.finki.mcptoolbackend.service.application.StatisticsApplicationService;
import mk.ukim.finki.mcptoolbackend.service.domain.ToolInvocationLogService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

/**
 * The <strong>fully-provided reference MCP tool</strong>. It reads aggregate
 * statistics from the database and returns them — so the whole MCP wiring
 * (dependency, config, security, tool registration, JSON serialization) works
 * end-to-end <em>before</em> you implement any {@code TODO(student)} tool.
 *
 * <p>Connect the MCP Inspector to {@code http://localhost:8080/sse} and invoke
 * {@code corpus_stats}: you should get a JSON object back. Use this class as the
 * template for the tools you must implement (see {@code ResourceSearchTool},
 * {@code ResourceAnalysisTool}, {@code DonationTool}).</p>
 */
@Service
public class CorpusStatsTool {
    private final StatisticsApplicationService statisticsApplicationService;
    private final ToolInvocationLogService toolInvocationLogService;

    public CorpusStatsTool(StatisticsApplicationService statisticsApplicationService,
                           ToolInvocationLogService toolInvocationLogService) {
        this.statisticsApplicationService = statisticsApplicationService;
        this.toolInvocationLogService = toolInvocationLogService;
    }

    @Tool(name = "corpus_stats",
        description = "Return aggregate statistics about the collected Macedonian-resource corpus: "
            + "the number of search runs, resources, analyzed resources, donation batches and donated resources.")
    public DisplayCorpusStatsDto corpusStats() {
        DisplayCorpusStatsDto stats = statisticsApplicationService.getStatistics();
        toolInvocationLogService.log(
            "corpus_stats",
            "{}",
            "resources=%d, searchRuns=%d, donations=%d".formatted(stats.resources(), stats.searchRuns(), stats.donationBatches()),
            true,
            null
        );
        return stats;
    }
}
