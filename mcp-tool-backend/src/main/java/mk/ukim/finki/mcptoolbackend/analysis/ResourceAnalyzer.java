package mk.ukim.finki.mcptoolbackend.analysis;

/**
 * The data-analysis seam of the MCP tool: turns a resource's raw text into a
 * summary, keywords and simple statistics.
 *
 * <p>TODO(student): Provide an implementation. It is deliberately
 * provider-agnostic — you may implement it with plain rule-based logic
 * (tokenizing, counting, frequency-based keyword extraction) or by delegating
 * to a language model (e.g. the Vezilka model). Keep any provider-specific
 * details (API keys, model names) inside the implementation.</p>
 */
public interface ResourceAnalyzer {
    /**
     * Analyzes the given resource text.
     *
     * @param title   the resource title (may be null)
     * @param content the resource's text content
     */
    AnalysisOutcome analyze(String title, String content);
}
