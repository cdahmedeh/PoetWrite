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

import java.util.List;

/**
 * A result of the computation for analyzing the phonemes of a word.
 *
 * This is computed in PhonemeComputer. See CmuEngine and MaryEngine for details
 * on how phonemes are calculated. The phonemes are in ARPAbet format.
 *
 * The convention of the *Analysis files is uncomputed parts are 'null' and get
 * filled if after the computation.
 *
 * @author Ahmed El-Hajjar
 */
@RequiredArgsConstructor
public class PhonemeAnalysis extends EntityAnalysis {
    @Getter
    private final Word word;

    @Getter @Setter
    private List<Phoneme> phonemes = null;

    @Override
    public boolean analyzed() {
        return phonemes != null;
    }
}
