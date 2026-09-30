package mk.ukim.finki.mcptoolbackend.analysis;

import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Map;

/**
 * Placeholder so the application boots before the assignment is implemented.
 * TODO(student): Replace this bean with a real Macedonian-language detector.
 */
@Component
public class StubLanguageDetector implements LanguageDetector {
    @Override
    public double macedonianConfidence(String text) {
//        throw new UnsupportedOperationException("TODO(student): Implement LanguageDetector.macedonianConfidence().");
        if (text==null || text.isBlank()){
            return 0.0;
        }

        String lowerCaseText=text.toLowerCase(Locale.ROOT);

        String[] macedonianWords={
                "кој","ги","го","не","за","да","се","која","како","може","дека","ова","има"
        };

        int matches=0;
        for (String macedonianWord:macedonianWords){
            if (lowerCaseText.contains(macedonianWord)){
                matches+=1;
            }
        }

        if (lowerCaseText.matches(".*[ѓќџѕљњ].*")){
            matches+=3;
        }

        return Math.min(matches/10.0,1.0);
    }

}
