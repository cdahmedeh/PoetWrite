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
 * Which verse of the poem a line sits in, and where in it.
 *
 * Keyed on the Line, though like the rhyme group it is really a fact about the
 * poem: you cannot know a line is in verse 2 without having read verse 1. Same
 * note applies, it should be reading a poem-level analysis once one exists.
 *
 * TODO: Verses are not in the entity structure at all yet. Poem holds a flat
 *       list of lines, so there is nothing to ask. Once blank lines are parsed
 *       into verse breaks this reads the real thing.
 */
public class LineVerseAnalysis extends FeatureAnalysis<Line> {
    public LineVerseAnalysis(Line line) {
        super(line);
    }

    // 1-based, matching the "Verse 1" heading in the editor. Zero means the
    // line is not part of any verse.
    @Getter @Setter
    private int verse = 0;

    // Which line of that verse, 1-based. Zero when unknown.
    @Getter @Setter
    private int position = 0;
}
