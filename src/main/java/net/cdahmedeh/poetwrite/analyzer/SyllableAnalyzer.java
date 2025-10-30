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

import net.cdahmedeh.poetwrite.analysis.WordAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.computer.WordComputer;
import net.cdahmedeh.poetwrite.domain.Word;

import javax.inject.Inject;

/**
 * @author Ahmed El-Hajjar
 *
 * The little tool that counts the number of syllables in a word (though for now
 * it accepts any text input. Right now, it's one way, text to syllables, but it
 * can't do the reverse by outputing the original word separate into syllables.
 *
 * This uses CMU database for counting, and if the word isn't in CMU, then we
 * go for a more heuristic approach using MaryTTS.
 *
 * This is a key part of the rhetoical analysis, especially for forms of poetry
 * where you want each line to have the same number of syllables.
 *
 * Unlike the engines which are internal use, this one is exposed via dependency
 * injection for use as a business logic.
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
 * 3.  This component, SyllableAnalyzer, runs the algorithm.
 */
public class SyllableAnalyzer {
    AnalysisCache analysisCache;

    @Inject
    /* package */ SyllableAnalyzer(AnalysisCache analysisCache) {
        this.analysisCache = analysisCache;
    }

    public int count(Word word) {
        WordAnalysis analysis = analysisCache.getWord(word);
        return analysis.getNumberOfSyllables();
    }
}
