package mk.ukim.finki.mcptoolbackend.mcp;

import mk.ukim.finki.mcptoolbackend.model.dto.DisplayResourceDto;
import mk.ukim.finki.mcptoolbackend.model.dto.DisplaySearchRunDto;
import mk.ukim.finki.mcptoolbackend.model.dto.RunSearchRequestDto;
import mk.ukim.finki.mcptoolbackend.model.exception.ResourceNotFoundException;
import mk.ukim.finki.mcptoolbackend.service.application.ResourceApplicationService;
import mk.ukim.finki.mcptoolbackend.service.application.SearchRunApplicationService;
import mk.ukim.finki.mcptoolbackend.service.domain.ToolInvocationLogService;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * MCP tools for searching the assigned website and reading stored resources.
 *
 * <p>TODO(student): Implement both tool methods. Follow {@link CorpusStatsTool}:
 * delegate to the injected application service, record the invocation with
 * {@link ToolInvocationLogService#log}, and return a JSON-serializable DTO
 * (never a JPA entity). Keep the {@code @Tool}/{@code @ToolParam} descriptions —
 * they become the tool's JSON Schema that the agent reads.</p>
 */
@Service
public class ResourceSearchTool {
    private final SearchRunApplicationService searchRunApplicationService;
    private final ResourceApplicationService resourceApplicationService;
    private final ToolInvocationLogService toolInvocationLogService;

    public ResourceSearchTool(SearchRunApplicationService searchRunApplicationService,
                              ResourceApplicationService resourceApplicationService,
                              ToolInvocationLogService toolInvocationLogService) {
        this.searchRunApplicationService = searchRunApplicationService;
        this.resourceApplicationService = resourceApplicationService;
        this.toolInvocationLogService = toolInvocationLogService;
    }

    @Tool(name = "search_resources",
        description = "Search the assigned Macedonian-language website for resources matching the query, "
            + "store them, and return the resulting search run (id, status, result count).")
    public DisplaySearchRunDto searchResources(
        @ToolParam(description = "Free-text search query, in Macedonian or English") String query,
        @ToolParam(description = "Maximum number of resources to fetch", required = false) Integer limit) {
        // TODO(student): delegate to searchRunApplicationService.run(new RunSearchRequestDto(query, limit)),
        //  log the invocation, and return the DTO.
        RunSearchRequestDto runSearchRequestDto=new RunSearchRequestDto(query,limit);
        DisplaySearchRunDto displaySearchRunDto=searchRunApplicationService.run(runSearchRequestDto).orElseThrow();

        toolInvocationLogService.log(
                "search_resources",
                "query=%s,limit=%s".formatted(query,limit),
                displaySearchRunDto.toString(),
                true,
                null
                );
        return displaySearchRunDto;
    }

    @Tool(name = "get_resource",
        description = "Fetch a single stored resource by its id, including its full text content and any analysis.")
    public DisplayResourceDto getResource(
        @ToolParam(description = "The id of the stored resource") Long id) {
        // TODO(student): delegate to resourceApplicationService.findById(id), log, and return the DTO.
        DisplayResourceDto displayResourceDto=resourceApplicationService.findById(id).orElseThrow(()->new ResourceNotFoundException(id));
        toolInvocationLogService.log(
                "get_resource",
                "id=%s".formatted(id),
                displayResourceDto.toString(),
                true,
                null
        );
        return displayResourceDto;
    }
}
