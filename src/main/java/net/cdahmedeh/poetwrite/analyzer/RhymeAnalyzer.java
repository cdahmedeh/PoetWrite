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

package net.cdahmedeh.poetwrite.analyzer;

import com.google.common.collect.Lists;
import net.cdahmedeh.poetwrite.analysis.RhymeAnalysis;
import net.cdahmedeh.poetwrite.analysis.WordAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.computer.WordComputer;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;

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
 * TODO: I'm still trying to decide where the magic should be done. It's a bit
 * all over the place right now.
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
    AnalysisCache analysisCache;

    @Inject
        /* package */ RhymeAnalyzer(AnalysisCache analysisCache) {
        this.analysisCache = analysisCache;
    }

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

    public int compare(Word wordA, Word wordB) {
        RhymeAnalysis analysis = analysisCache.getRhyme(wordA, wordB);
        return analysis.getNumberOfRhymeSyllables();
    }
}
