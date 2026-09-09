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
import net.cdahmedeh.poetwrite.lib.analysis.LineVerseAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.tools.SleepTools;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Works out which verse a line sits in.
 *
 * Hard-coded. It says verse 1 for everything, because the entity structure has
 * no idea what a verse is yet, and guessing from line numbers would be a lie
 * dressed up as an answer.
 *
 * Kept as its own analyzer anyway rather than folded into another one, because
 * that is where the real thing will go and the tooltip should not have to
 * change when it arrives.
 *
 * TODO: Poem holds a flat list of lines, so blank lines are not verse breaks
 *       to it. Once the parser groups lines into verses this reads the real
 *       structure and the position field starts meaning something.
 */
@Draft("Always verse 1, the entity structure has no verses yet")
@Singleton
public class VerseAnalyzer extends FeatureAnalyzer<Line, LineVerseAnalysis> {

    // Only here so the loading row is visible while developing. The slowest of
    // the gutter rows on purpose, so the tooltip visibly fills in.
    private static final long FAKE_GROUPING_MILLIS = 1400;

    @Inject
    VerseAnalyzer(AnalysisCache analysisCache, TaskBus taskBus) {
        super(analysisCache, taskBus);
    }

    public LineVerseAnalysis get(Line line) {
        return get(line, LineVerseAnalysis.class);
    }

    @Override
    /* package */ void analyze(Line line, LineVerseAnalysis analysis) {
        SleepTools.safeSleep(FAKE_GROUPING_MILLIS);

        analysis.setVerse(1);
        analysis.setPosition(0);
    }

    @Override
    public String name() {
        return "Verse Analyzer";
    }

    @Override
    protected void init() {

    }
}
