/**
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

package net.cdahmedeh.poetwrite.analyzer;

import com.google.common.collect.Lists;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;
import net.cdahmedeh.poetwrite.engine.cmu.CmuEngine;
import net.cdahmedeh.poetwrite.engine.mary.MaryEngine;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;

/**
 * @author Ahmed El-Hajjar
 *
 * For counting the number of syllables in a rhyme. Takes two words, checks if
 * they rhymes, and returns the number of syllables they have in common. See
 * the compareWords(..) comment for the algorithm.
 *
 * I'm still trying to decide where the magic should be done. It's a bit all
 * over the place right now.
 *
 * 1a. CmuEngine will load the CMU database in memory.
 * 1b. MaryEngine will load the MaryTTS server
 *
 * 2a. CmuEngine precomputes the phonemes using WordConstructor which parses
 *     the entries.
 * 2b. MaryEngine only computers the phonemes on word lookup.
 *
 * 3.  This component, RhymeAnalyzer, runs the algorithm.
 */
public class RhymeAnalyzer {
    /* package */ CmuEngine cmuEngine;
    /* package */ MaryEngine maryEngine;

    @Inject
    /* package */ RhymeAnalyzer(CmuEngine cmuEngine, MaryEngine maryEngine) {
        this.cmuEngine = cmuEngine;
        this.maryEngine = maryEngine;
    }

    /**
     * This just pulls in the words from the engines and uses the magic
     * compareWords(..) method does to compare them. See the comments above that
     * method for the algorithm.
     *
     * There's a hidden assumption that textA and textB are clean single word
     * with only lowercase characters, and no numbers or symbols.
     */
    public int compare(String textA, String textB) {
        Word wordA = getSafeWord(textA);
        Word wordB = getSafeWord(textB);

        return compareWords(wordA, wordB);
    }

    /**
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
     *
     */
    private int compareWords(Word word1, Word word2) {
        List<Phoneme> phonemes1 = Lists.reverse(word1.getPhonemes());
        List<Phoneme> phonemes2 = Lists.reverse(word2.getPhonemes());

        int loopSize = Math.min(phonemes1.size(), phonemes2.size());

        int syllableCount = 0;

        for (int i = 0; i < loopSize;  i++) {
            String phone1 = phonemes1.get(i).getPhone();
            String phone2 = phonemes2.get(i).getPhone();

            if (Objects.equals(phone1, phone2) == false) {
                break;
            }

            if (phonemes1.get(i).isVowel() && phonemes2.get(i).isVowel()) {
                syllableCount++;
            }
        }

        return syllableCount;
    }

    /**
     * Gets the word from CMU, and if it's not in CMU, get MaryTTS to do its
     * thing.
     *
     * Not foolproof.
     */
    private Word getSafeWord(String text) {
        if (cmuEngine.hasWord(text)) {
            return cmuEngine.getWord(text);
        } else {
            return maryEngine.getWord(text);
        }
    }
}
