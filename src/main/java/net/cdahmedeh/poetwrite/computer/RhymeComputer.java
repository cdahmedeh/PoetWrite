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

import com.google.common.collect.Lists;
import net.cdahmedeh.poetwrite.analysis.RhymeAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;
import net.cdahmedeh.poetwrite.engine.CmuEngine;
import net.cdahmedeh.poetwrite.engine.MaryEngine;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Ahmed El-Hajjar
 *
 * This analyzer does some rhyming analysis and outputs a result as
 * a RhymeAnalysis object. The output is stored in the AnalysisCache which can
 * be retrieved later.
 *
 * DESIGN: As I mentioned in AnalysisCache, only these *Computer classes can
 *         access the AnalysisCache. Anything that needs to read from the cache
 *         has to do it through here.
 *
 * SOURCE: The phonemes are calculated by a lookup in the CMU dictionary, and if
 *         the word doesn't exist, it will the MaryTTS engine using a heuristic
 *         method.
 *
 * ALGORITHM:
 * Right now, all we are trying to calculate is the amount of common syllables
 * in a rhyme for a given pair of two words.
 *
 * calculation speculation
 *    11223344    11223344
 * The above has 4 syllables that rhyme.
 *

 * Let's take this example with comparing calculation and speculation. We
 *                     Matching phonemes = Rhyming
 *                      | | ||| | ||| || ||| |
 *                      V V VVV V VVV VV VVV V
 * CALCULATION  K AE2 L K Y AH0 L EY1 SH AH0 N
 * SPECULATION  S P EH2 K Y AH0 L EY1 SH AH0 N
 *
 * If you notice, if you go backwords through the phonemes, and keep
 * checking they match until you find the first phonemes that don't match.
 * The number of rhyming syllables basically is the number of vowels
 * through that traversal. This is roughly what the algorithm below does.
 *
 * Check RhymeTest for additional examples.
 *
 * TODO: This is really dirty right now. It's neither readable nor optmized.
 * TODO: Partial Rhymes, Slant Rhymes
 * TODO: Have the specific phonemes that rhyme.
 * TODO: Have the syllables in text that rhyme.
 */
public class RhymeComputer {
    AnalysisCache analysisCache;

    CmuEngine cmuEngine;
    MaryEngine maryEngine;

    @Inject
    RhymeComputer(
            AnalysisCache analysisCache,
            CmuEngine cmuEngine,
            MaryEngine maryEngine) {
        this.analysisCache = analysisCache;
        this.cmuEngine = cmuEngine;
        this.maryEngine = maryEngine;
    }

    public void analyze(Word wordA, Word wordB) {
        RhymeAnalysis analysis = analysisCache.getRhyme(wordA, wordB);

        analyzeRhyme(wordA, wordB, analysis);
    }

    public RhymeAnalysis getRhyme(Word wordA, Word wordB) {
        RhymeAnalysis analysis = analysisCache.getRhyme(wordA, wordB);
        return analysis;
    }

    private void analyzeRhyme(Word wordA, Word wordB, RhymeAnalysis analysis) {
        if (analysis.isRhymeAnalyzed() == false) {
            if (analysis.arePhonemesAnalyzed() == false) {
                analyzePhonemes(wordA, wordB, analysis);
            }

            List<Phoneme> phonemesA = Lists.reverse(analysis.getPhonemesA());
            List<Phoneme> phonemesB = Lists.reverse(analysis.getPhonemesB());

            int loopSize = Math.min(phonemesA.size(), phonemesB.size());

            int syllableCount = 0;

            for (int i = 0; i < loopSize;  i++) {
                String phone1 = phonemesA.get(i).getPhone();
                String phone2 = phonemesB.get(i).getPhone();

                if (Objects.equals(phone1, phone2) == false) {
                    break;
                }

                if (phonemesA.get(i).isVowel() && phonemesB.get(i).isVowel()) {
                    syllableCount++;
                }
            }

            analysis.setNumberOfRhymeSyllables(syllableCount);
        }
    }

    private void analyzePhonemes(Word wordA, Word wordB, RhymeAnalysis analysis) {
        if (analysis.arePhonemesAnalyzed() == false) {
            List<Phoneme> phonemesA = new ArrayList<>();
            phonemesA.addAll(getPhonemes(wordA));
            analysis.setPhonemesA(phonemesA);

            List<Phoneme> phonemesB = new ArrayList<>();
            phonemesB.addAll(getPhonemes(wordB));
            analysis.setPhonemesB(phonemesB);
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
