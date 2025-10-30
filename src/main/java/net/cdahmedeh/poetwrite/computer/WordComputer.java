/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2025 Ahmed El-Hajjar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.cdahmedeh.poetwrite.computer;

import net.cdahmedeh.poetwrite.analysis.RhymeAnalysis;
import net.cdahmedeh.poetwrite.analysis.WordAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;
import net.cdahmedeh.poetwrite.engine.CmuEngine;
import net.cdahmedeh.poetwrite.engine.MaryEngine;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class WordComputer {
    AnalysisCache analysisCache;

    CmuEngine cmuEngine;
    MaryEngine maryEngine;

    @Inject
    WordComputer(
            AnalysisCache analysisCache,
            CmuEngine cmuEngine,
            MaryEngine maryEngine) {
        this.analysisCache = analysisCache;
        this.cmuEngine = cmuEngine;
        this.maryEngine = maryEngine;
    }

    public void analyze(Word word) {
        WordAnalysis analysis = analysisCache.getWord(word);

        analyzePhonemes(word, analysis);
    }

    public WordAnalysis getWord(Word word) {
        WordAnalysis analysis = analysisCache.getWord(word);
        return analysis;
    }

    private void analyzePhonemes(Word word, WordAnalysis analysis) {
        if (analysis.arePhonemesAnalyzed() == false) {
            List<Phoneme> phonemes = new ArrayList<>();
            phonemes.addAll(getPhonemes(word));
            analysis.setPhonemes(phonemes);

            int syllables = (int) phonemes.stream()
                    .filter(Phoneme::isVowel)
                    .count();

            analysis.setNumberOfSyllables(syllables);
        }
    }

    private List<Phoneme> getPhonemes(Word word) {
        if (cmuEngine.hasWord(word)) {
            return cmuEngine.getPhonemes(word);
        } else {
            return maryEngine.getPhonemes(word);
        }
    }
}
