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

package net.cdahmedeh.poetwrite.analysis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;
import net.cdahmedeh.poetwrite.domain.WordPair;

import java.util.List;

/**
 * A result of the computation for analyzing the rhyme of two words.
 *
 * This is computed in RhymeComputer. See class comments for how the analysis is
 * actually done.
 *
 * Right now, the focus is just on basic rhyming with just counting how many
 * syllables are rhymed with in the provided word pair.
 *
 * The convention of the *Analysis files is uncomputed parts are 'null' and get
 * filled if after the computation.
 *
 * TODO: Consider using a pair object for word pairs.
 * TODO: Find a way to make word pairs reversed to be equal.
 * TODO: Partial Rhymes, Slant Rhymes
 * TODO: Have the specific phonemes that rhyme.
 * TODO: Have the syllables in text that rhyme.
 *
 * @author Ahmed El-Hajjar
 *
 */
@RequiredArgsConstructor
public class RhymeAnalysis extends EntityAnalysis {
    private final WordPair wordPair;

    public RhymeAnalysis(Word word1, Word word2) {
        this(new WordPair(word1, word2));
    }

    @Getter @Setter
    private Integer numberOfRhymeSyllables = null;

    @Override
    public boolean analyzed() {
        return numberOfRhymeSyllables != null;
    }
}
