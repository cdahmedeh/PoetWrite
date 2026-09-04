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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.cdahmedeh.poetwrite.lib.domain.Line;

/**
 * The meter of a line.
 *
 * Keyed on the Line, and unlike the part of speech this one is not even about
 * the hovered word. Scansion is a property of the line itself, the word just
 * happens to sit in it. The tooltip shows it because it is useful to know what
 * you are writing into while you are writing it.
 *
 * TODO: I'm just putting this as a placeholder for now. I don't have the
 *       metering system fully flesched out.
 * TODO: The real one will want the stress pattern too, not just the name, so
 *       the gutter and the tooltip can both read off the same analysis.
 */
@RequiredArgsConstructor
public class LineMeterAnalysis extends FeatureAnalysis {
    @Getter
    private final Line line;

    @Getter @Setter
    private String meter = null;

    @Override
    public boolean analyzed() {
        return meter != null;
    }
}
