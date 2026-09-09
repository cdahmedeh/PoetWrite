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
import lombok.Setter;
import net.cdahmedeh.poetwrite.lib.domain.Line;

/**
 * Which rhyme group a line belongs to, for the gutter tooltip.
 *
 * The letter is what the gutter already paints in its pattern column. The
 * sound is what that group actually rhymes on, which the gutter has no room
 * for but the tooltip does. "A" on its own tells you two lines rhyme, "A -
 * ions" tells you what they rhyme on.
 *
 * Keyed on the Line, though that is not really the truth of it: a rhyme group
 * is a property of the whole poem, since a line only has a letter relative to
 * the other lines. PatternAnalysis already works that way and computes the
 * whole scheme at once. This is the per-line view of that, and once it stops
 * being hard-coded it should be reading PatternAnalysis rather than working
 * anything out itself.
 */
public class LineRhymeGroupAnalysis extends FeatureAnalysis<Line> {
    public LineRhymeGroupAnalysis(Line line) {
        super(line);
    }

    // The gutter letter. Null when the line is not in any group, which is the
    // usual case for a line whose ending nothing else matches.
    @Getter @Setter
    private String group = null;

    // What the group rhymes on, e.g. "ions". Null when there is no group.
    @Getter @Setter
    private String sound = null;
}
