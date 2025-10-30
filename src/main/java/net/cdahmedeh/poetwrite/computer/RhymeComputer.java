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
import net.cdahmedeh.poetwrite.analysis.PhonemeAnalysis;
import net.cdahmedeh.poetwrite.analysis.RhymeAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;

import javax.inject.Inject;
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
 * SOURCES: The phonemes are calculated as part of the PhonemeComputer which
 *          uses CMU or MaryTTS engines stored in ARPAbet format.
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

    PhonemeComputer phonemeComputer;

    @Inject
    RhymeComputer(
            AnalysisCache analysisCache,
            PhonemeComputer phonemeComputer) {
        this.analysisCache = analysisCache;
        this.phonemeComputer = phonemeComputer;
    }

    public RhymeAnalysis get(Word wordA, Word wordB) {
        RhymeAnalysis analysis = analysisCache.getRhyme(wordA, wordB);

        if (analysis.analyzed() == false) {
            analyze(wordA, wordB, analysis);
        }
        return analysis;
    }

    private void analyze(Word wordA, Word wordB, RhymeAnalysis analysis) {
        PhonemeAnalysis phonemeAnalysisA = phonemeComputer.get(wordA);
        PhonemeAnalysis phonemeAnalysisB = phonemeComputer.get(wordB);

        List<Phoneme> phonemesA = Lists.reverse(phonemeAnalysisA.getPhonemes());
        List<Phoneme> phonemesB = Lists.reverse(phonemeAnalysisB.getPhonemes());

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
