package net.cdahmedeh.poetwrite.cache;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import lombok.Getter;
import net.cdahmedeh.poetwrite.analysis.RhymeAnalysis;
import net.cdahmedeh.poetwrite.analysis.WordAnalysis;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class AnalysisCache {

    private Map<Word, WordAnalysis> words = new HashMap<>();

    private Table<Word, Word, RhymeAnalysis> rhymes = HashBasedTable.create();

    @Inject
    public AnalysisCache() {

    }

    public WordAnalysis getWord(Word word) {
        WordAnalysis analysis = words.get(word);

        if (analysis == null) {
            analysis = new WordAnalysis(word);
            words.put(word, analysis);
        }

        return analysis;
    }

    public RhymeAnalysis getRhyme(Word wordA, Word wordB) {
        RhymeAnalysis rhyme = rhymes.get(wordA, wordB);

        if (rhyme == null) {
            rhyme = new RhymeAnalysis(wordA, wordB);
            rhymes.put(wordA, wordB, rhyme);
        }

        return rhyme;
    }
}
