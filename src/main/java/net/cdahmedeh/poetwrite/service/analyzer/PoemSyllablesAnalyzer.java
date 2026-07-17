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

import net.cdahmedeh.poetwrite.lib.analysis.LineAnalysis;
import net.cdahmedeh.poetwrite.lib.analysis.PoemSyllablesAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.lib.domain.Poem;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Counts how many syllables are in the line for an entire poem.
 *
 * TODO: Several notes sprinkley about whether line per line analysis or for
 *       the whole poem. Has been pretty fast already.
 */
@Singleton
public class PoemSyllablesAnalyzer extends FeatureAnalyzer<Poem, PoemSyllablesAnalysis> {
    private final LineAnalyzer lineAnalyzer;

    @Inject
    public PoemSyllablesAnalyzer(AnalysisCache analysisCache, TaskBus taskBus, LineAnalyzer lineAnalyzer) {
        super(analysisCache, taskBus);
        this.lineAnalyzer = lineAnalyzer;
    }

    @Override
    void analyze(Poem entity, PoemSyllablesAnalysis analysis) {
        List<Line> lines = entity.getLines();
        List<Integer> counts = new ArrayList<>();

        for  (Line line : lines) {
            LineAnalysis lineAnalysis = lineAnalyzer.get(line);
            int syllables = lineAnalysis.getTotalSyllables();
            counts.add(syllables);
        }

        analysis.getSyllables().addAll(counts);
    }

    public PoemSyllablesAnalysis get(Poem entity) {
        return get(entity, PoemSyllablesAnalysis.class);
    }

    @Override
    public String name() {
        return "Poem Syllables Analyzer";
    }

    @Override
    protected void init() {

    }
}
