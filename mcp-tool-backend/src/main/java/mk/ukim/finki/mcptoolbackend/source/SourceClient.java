package mk.ukim.finki.mcptoolbackend.source;

import java.util.List;

/**
 * The data-source seam: how the MCP tool reaches the specific Macedonian-resource
 * website assigned to you. This is the analog of the reference project's
 * {@code BrowserAgent} — but for a search-and-fetch tool it is usually far
 * simpler (an HTTP request plus HTML parsing, or the site's own API).
 *
 * <p>TODO(student): Provide an implementation for YOUR assigned website,
 * configured through {@link SourceProperties}. Add a parsing library such as
 * {@code org.jsoup:jsoup} to the pom if you scrape HTML. Keep site-specific
 * details (selectors, endpoints, auth) inside the implementation.</p>
 */
public interface SourceClient {
    /**
     * Searches the website and returns matching resources (at most {@code limit}).
     */
    List<FetchedResource> search(String query, int limit);

    /**
     * Fetches the full content of a single resource by its absolute URL.
     */
    FetchedResource fetch(String url);
}
