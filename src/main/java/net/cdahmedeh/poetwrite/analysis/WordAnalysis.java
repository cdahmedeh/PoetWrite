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
import net.cdahmedeh.poetwrite.domain.Word;

/**
 * A result of the computation for related to the phonemes of a word such as
 * syllables in the word.
 *
 * This is computed in WordComputer. See class comments for how the analysis is
 * actually done.
 *
 * Right now, the phonemes themselves are part of the PhonemeAnalysis.
 *
 * The convention of the *Analysis files is uncomputed parts are 'null' and get
 * filled if after the computation.
 *
 * @author Ahmed El-Hajjar
 *
 */
@RequiredArgsConstructor
public class WordAnalysis {
    @Getter
    private final Word word;

    @Getter @Setter
    private Integer numberOfSyllables = null;

    public boolean analyzed() {
        return numberOfSyllables != null;
    }
}
