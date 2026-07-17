/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2026 Ahmed El-Hajjar
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

package net.cdahmedeh.poetwrite.lib.analysis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.cdahmedeh.poetwrite.lib.domain.Line;

/**
 * Analysis for a line in the poem.
 *
 * Current only has the number of syllables in a line.
 */
@RequiredArgsConstructor
public class LineAnalysis extends FeatureAnalysis {
    @Getter
    private final Line line;

    @Getter @Setter
    private int totalSyllables = -1;

    @Override
    public boolean analyzed() {
        return totalSyllables != -1;
    }
}
