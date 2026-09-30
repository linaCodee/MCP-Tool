package mk.ukim.finki.mcptoolbackend.analysis;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with a real analyzer.
 */
@Component
public class StubResourceAnalyzer implements ResourceAnalyzer {
    @Override
    public AnalysisOutcome analyze(String title, String content) {
//        throw new UnsupportedOperationException("TODO(student): Implement ResourceAnalyzer.analyze().");

        if (title==null || title.isBlank() || content==null || content.isBlank()){
            return new AnalysisOutcome("", List.of(),0,0);
        }

        String fullText=(title.trim()+". ")+content.trim();

        String[] sentences=fullText.split("[.?!]+");
        int sentenceCount=sentences.length;

        // se sto ne e bukva ili prazno mesto se brise zamenuvajki so prazno mesto
        // ex. ? se zamenuva so prazno mesto
        String[] words=fullText.toLowerCase().replaceAll("[^\\p{L}\\s]"," ").trim().split("\\s+");
        int wordCount=words.length;

        String summary=sentences.length>1 ? sentences[1].trim() : sentences[0].trim();

        List<String> keywords= Arrays.stream(words).filter(w->w.length()>4).distinct().limit(5).toList();

        return new AnalysisOutcome(summary,keywords,sentenceCount,wordCount);
    }
}
