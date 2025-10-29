package net.cdahmedeh.poetwrite.cache;

import lombok.Getter;
import net.cdahmedeh.poetwrite.analysis.WordAnalysis;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;

import java.util.HashMap;
import java.util.Map;

public class AnalysisCache {
    public static AnalysisCache instance = new AnalysisCache();

    private Map<Word, WordAnalysis> words = new HashMap<>();

    public WordAnalysis getWord(Word word) {
        if (words.containsKey(word)) {
            return words.get(word);
        } else {
            WordAnalysis analysis = new WordAnalysis(word);
            words.put(word, analysis);
            return analysis;
        }
    }

    public void putWord(Word word, WordAnalysis analysis) {
        words.put(word, analysis);
    }
}
