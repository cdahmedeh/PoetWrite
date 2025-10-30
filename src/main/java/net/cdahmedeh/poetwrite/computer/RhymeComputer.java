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
import net.cdahmedeh.poetwrite.analysis.WordAnalysis;
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
 * This just pulls in the words from the engines and uses the magic
 * compareWords(..) method does to compare them. See the comments above that
 * method for the algorithm.
 *
 * There's a hidden assumption that textA and textB are clean single word
 * with only lowercase characters, and no numbers or symbols.
 *
 * Compares two words and returns how many syllables rhyme.
 * calculation speculation
 *    11223344    11223344
 * The above has 4 syllables that rhyme.
 *
 * Check RhymeTest for additional examples.
 *
 * This is really dirty right now. It's neither readable nor optmized.
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
