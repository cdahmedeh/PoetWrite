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
import net.cdahmedeh.poetwrite.lib.analysis.SyllableAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.lib.domain.Node;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.lib.domain.Words;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.service.engine.CmuEngine;
import net.cdahmedeh.poetwrite.service.engine.MaryEngine;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Used to count how many syllables are in a line.
 *
 * TODO: Consider renaming as it seems like seperate analysis will be in their
 *       own analysis class.
 */
@Singleton
public class LineAnalyzer extends FeatureAnalyzer<Line, LineAnalysis> {
    private final SyllableAnalyzer syllableAnalyzer;

    @Inject
    LineAnalyzer(
            AnalysisCache analysisCache,
            TaskBus taskBus,
            SyllableAnalyzer syllableAnalyzer) {
        super(analysisCache, taskBus);
        this.syllableAnalyzer = syllableAnalyzer;
    }

    public LineAnalysis get(Line line) {
        return get(line, LineAnalysis.class);
    }

    @Override
    void analyze(Line line, LineAnalysis analysis) {
        int syllables = 0;

        List<Node> nodes = line.getNodes();

        for (Node node : nodes) {
            if (node instanceof Words words) {
                for (Word word : words.getWords()) {
                    SyllableAnalysis syllableAnalysis = syllableAnalyzer.get(word);
                    syllables += syllableAnalysis.getNumberOfSyllables();
                }
            }
        }

        analysis.setTotalSyllables(syllables);
    }

    @Override
    public String name() {
        return "Line Analyzer";
    }

    @Override
    protected void init() {

    }
}
