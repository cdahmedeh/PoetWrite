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

package net.cdahmedeh.poetwrite.service.analyzer;

import net.cdahmedeh.poetwrite.annotation.Draft;
import net.cdahmedeh.poetwrite.lib.analysis.LineRhymeGroupAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.tools.SleepTools;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Works out which rhyme group a line belongs to.
 *
 * Hard-coded, same as the other tooltip analyzers. It picks a group off a list
 * based on the last word of the line, purely so the answer changes from line
 * to line instead of showing the same thing everywhere. No rhyme comparison is
 * happening.
 *
 * TODO: PatternAnalyzer already does this properly for the gutter's pattern
 *       column, across the whole poem in one pass. The real version of this
 *       reads that instead of working anything out, which also means the
 *       tooltip and the gutter column can never disagree.
 */
@Draft("Hard-coded rhyme groups, no rhyme comparison happening")
@Singleton
public class RhymeGroupAnalyzer extends FeatureAnalyzer<Line, LineRhymeGroupAnalysis> {

    // Only here so the loading row is visible while developing.
    private static final long FAKE_MATCHING_MILLIS = 700;

    private static final List<String> GROUPS = List.of("A", "B", "C");
    private static final List<String> SOUNDS = List.of("ions", "ess", "ight");

    @Inject
    RhymeGroupAnalyzer(AnalysisCache analysisCache, TaskBus taskBus) {
        super(analysisCache, taskBus);
    }

    public LineRhymeGroupAnalysis get(Line line) {
        return get(line, LineRhymeGroupAnalysis.class);
    }

    @Override
    /* package */ void analyze(Line line, LineRhymeGroupAnalysis analysis) {
        SleepTools.safeSleep(FAKE_MATCHING_MILLIS);

        Word ending = line.getLastWord();

        // A line with nothing in it cannot be in a group, and saying so is
        // more useful than inventing one.
        if (ending == null) {
            return;
        }

        int index = Math.floorMod(ending.getWord().hashCode(), GROUPS.size());

        analysis.setGroup(GROUPS.get(index));
        analysis.setSound(SOUNDS.get(index));
    }

    @Override
    public String name() {
        return "Rhyme Group Analyzer";
    }

    @Override
    protected void init() {

    }
}
