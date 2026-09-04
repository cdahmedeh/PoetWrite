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
import net.cdahmedeh.poetwrite.lib.analysis.LineMeterAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.tools.SleepTools;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Calculates the meter of a line. The term for the analysis of a meter is
 * called a scansion.
 *
 * TODO: The metering system is not flesched out at all. I'm just putting this
 *       as a place-holder for demo purposes. Fake sleep.
 */
@Draft("Hard-coded meter names, no scansion happening")
@Singleton
public class MeterAnalyzer extends FeatureAnalyzer<Line, LineMeterAnalysis> {

    private static final long FAKE_SCANSION_MILLIS = 1200;

    private static final List<String> DEMO_METERS = List.of(
            "iambic dimeter",
            "iambic trimeter",
            "iambic tetrameter",
            "iambic pentameter",
            "iambic hexameter");

    @Inject
    MeterAnalyzer(AnalysisCache analysisCache, TaskBus taskBus) {
        super(analysisCache, taskBus);
    }

    public LineMeterAnalysis get(Line line) {
        return get(line, LineMeterAnalysis.class);
    }

    @Override
    /* package */ void analyze(Line line, LineMeterAnalysis analysis) {
        SleepTools.safeSleep(FAKE_SCANSION_MILLIS);

        int words = line.getAllWords().size();

        analysis.setMeter(words == 0
                ? "no meter"
                : DEMO_METERS.get(Math.min(words, DEMO_METERS.size()) - 1));
    }

    @Override
    public String name() {
        return "Meter Analyzer";
    }

    @Override
    protected void init() {

    }
}
