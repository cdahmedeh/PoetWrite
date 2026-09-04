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
import net.cdahmedeh.poetwrite.lib.analysis.WordDefinitionAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.tools.SleepTools;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * Pulls in the meaning of the word from a dictionary. It is used in both
 * the hover tooltip and the autocomplete preview.
 *
 * TODO: Currently hard-coded for demo purposes. Changing it to use an actual
 *       dictionary provider will be really easy.
 */
@Draft("Hard-coded dictionary until a real one is wired in")
@Singleton
public class DefinitionAnalyzer extends FeatureAnalyzer<Word, WordDefinitionAnalysis> {

    private static final long FAKE_LOOKUP_MILLIS = 900;

    private static final Map<String, String> DEMO_DICTIONARY = Map.of(
            "darkness", "The partial or total absence of light.",
            "fortress", "A military stronghold.",
            "moon", "The natural satellite of the earth.",
            "night", "The period of darkness between sunset and sunrise.",
            "starless", "Without visible stars.",
            "quiet", "Making little or no noise.");

    @Inject
    DefinitionAnalyzer(AnalysisCache analysisCache, TaskBus taskBus) {
        super(analysisCache, taskBus);
    }

    public WordDefinitionAnalysis get(Word word) {
        return get(word, WordDefinitionAnalysis.class);
    }

    @Override
    /* package */ void analyze(Word word, WordDefinitionAnalysis analysis) {
        SleepTools.safeSleep(FAKE_LOOKUP_MILLIS);

        String definition = DEMO_DICTIONARY.get(word.getWord());

        analysis.setDefinition(definition == null ? "No entry." : definition);
    }

    @Override
    public String name() {
        return "Definition Analyzer";
    }

    @Override
    protected void init() {

    }
}
