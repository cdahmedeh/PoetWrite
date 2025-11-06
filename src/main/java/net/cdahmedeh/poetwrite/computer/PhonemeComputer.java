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

import net.cdahmedeh.poetwrite.analysis.PhonemeAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;
import net.cdahmedeh.poetwrite.engine.CmuEngine;
import net.cdahmedeh.poetwrite.engine.MaryEngine;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ahmed El-Hajjar
 *
 * This analyzer does some phoneme analysis and outputs a result as a
 * PhonemeAnalysis object. The output is stored in the AnalysisCache which can
 * retrieved later.
 *
 * SOURCE: The phonemes are calculated by a lookup in the CMU dictionary, and if
 *         the word doesn't exist, it will the MaryTTS engine using a heuristic
 *         method.
 */
public class PhonemeComputer extends EntityComputer<Word, PhonemeAnalysis> {
    CmuEngine cmuEngine;
    MaryEngine maryEngine;

    @Inject
    PhonemeComputer(
            AnalysisCache analysisCache,
            CmuEngine cmuEngine,
            MaryEngine maryEngine) {
        super(analysisCache);
        this.cmuEngine = cmuEngine;
        this.maryEngine = maryEngine;
    }

    public PhonemeAnalysis get(Word word) {
        return get(word, PhonemeAnalysis.class);
    }

    @Override
    /* package */ void analyze(Word word, PhonemeAnalysis analysis) {
        List<Phoneme> phonemes = new ArrayList<>();

        if (cmuEngine.hasWord(word)) {
            phonemes.addAll(cmuEngine.getPhonemes(word));
        } else {
            phonemes.addAll(maryEngine.getPhonemes(word));
        }

        analysis.setPhonemes(phonemes);
    }
}
