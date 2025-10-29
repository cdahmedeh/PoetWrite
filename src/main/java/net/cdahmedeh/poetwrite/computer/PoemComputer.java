package net.cdahmedeh.poetwrite.computer;

import net.cdahmedeh.poetwrite.analysis.WordAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.constant.PhonemeConstants;
import net.cdahmedeh.poetwrite.domain.Line;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Poem;
import net.cdahmedeh.poetwrite.domain.Word;
import net.cdahmedeh.poetwrite.engine.CmuEngine;
import net.cdahmedeh.poetwrite.engine.MaryEngine;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class PoemComputer {
    AnalysisCache analysisCache = AnalysisCache.instance;

    CmuEngine cmuEngine;
    MaryEngine maryEngine;

    @Inject
    PoemComputer(
            CmuEngine cmuEngine,
            MaryEngine maryEngine) {
        this.cmuEngine = cmuEngine;
        this.maryEngine = maryEngine;
    }

    public void analyzeWordPhonemes(Word word) {
        WordAnalysis analysis = analysisCache.getWord(word);

        if (analysis.isWordAnalyzed() == false) {
            List<Phoneme> phonemes = new ArrayList<>();
            phonemes.addAll(getSafeWord(word.getWord()));
            analysis.setPhonemes(phonemes);

            int syllables = (int) phonemes.stream()
                    .filter(Phoneme::isVowel)
                    .count();

            analysis.setNumberOfSyllables(syllables);

            analysisCache.putWord(word, analysis);
        }
    }

    private List<Phoneme> getSafeWord(String text) {
        if (cmuEngine.hasWord(text)) {
            return cmuEngine.getWord(new Word(text));
        } else {
            return maryEngine.getWord(new Word(text));
        }
    }
}
