package net.cdahmedeh.poetwrite.computer;

import net.cdahmedeh.poetwrite.analysis.PhonemeAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;
import net.cdahmedeh.poetwrite.engine.CmuEngine;
import net.cdahmedeh.poetwrite.engine.MaryEngine;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class PhonemeComputer {
    AnalysisCache analysisCache;

    CmuEngine cmuEngine;
    MaryEngine maryEngine;

    @Inject
    PhonemeComputer(
            AnalysisCache analysisCache,
            CmuEngine cmuEngine,
            MaryEngine maryEngine) {
        this.analysisCache = analysisCache;
        this.cmuEngine = cmuEngine;
        this.maryEngine = maryEngine;
    }

    public PhonemeAnalysis get(Word word) {
        PhonemeAnalysis analysis = analysisCache.getPhoneme(word);

        if (analysis.analyzed() == false) {
            analyze(word, analysis);
        }
        return analysis;
    }

    private void analyze(Word word, PhonemeAnalysis analysis) {
        List<Phoneme> phonemes = new ArrayList<>();

        if (cmuEngine.hasWord(word)) {
            phonemes.addAll(cmuEngine.getPhonemes(word));
        } else {
            phonemes.addAll(maryEngine.getPhonemes(word));
        }

        analysis.setPhonemes(phonemes);
    }
}
