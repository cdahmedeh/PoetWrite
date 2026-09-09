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
import net.cdahmedeh.poetwrite.lib.analysis.WordSynonymsAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.tools.SleepTools;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

/**
 * Looks up words that mean roughly the same thing.
 *
 * Hard-coded thesaurus, same as DefinitionAnalyzer. When a real one turns up
 * it goes in the constructor next to AnalysisCache and analyze(..) calls it,
 * and nothing above this class has to change.
 *
 * The sleep is only there so I can actually see the Loading row while
 * developing.
 */
@Draft("Hard-coded thesaurus until a real one is wired in")
@Singleton
public class SynonymAnalyzer extends FeatureAnalyzer<Word, WordSynonymsAnalysis> {

    private static final long FAKE_LOOKUP_MILLIS = 1100;

    private static final Map<String, List<String>> DEMO_THESAURUS = Map.of(
            "attention", List.of("notice", "regard", "heed", "scrutiny"),
            "devotion", List.of("dedication", "allegiance", "piety", "fidelity"),
            "motion", List.of("movement", "passage", "drift", "travel"),
            "unspeakable", List.of("unutterable", "ineffable", "dreadful", "nameless"),
            "fadeable", List.of("perishable", "impermanent", "transient"),
            "blackness", List.of("gloom", "murk", "pitch", "obscurity"),
            "vividness", List.of("brilliance", "intensity", "clarity", "sharpness"),
            "fortress", List.of("citadel", "stronghold", "bastion", "keep"),
            "darkness", List.of("gloom", "murk", "shadow", "obscurity"));

    @Inject
    SynonymAnalyzer(AnalysisCache analysisCache, TaskBus taskBus) {
        super(analysisCache, taskBus);
    }

    public WordSynonymsAnalysis get(Word word) {
        return get(word, WordSynonymsAnalysis.class);
    }

    @Override
    /* package */ void analyze(Word word, WordSynonymsAnalysis analysis) {
        SleepTools.safeSleep(FAKE_LOOKUP_MILLIS);

        analysis.setSynonyms(DEMO_THESAURUS.getOrDefault(word.getWord(), List.of()));
    }

    @Override
    public String name() {
        return "Synonym Analyzer";
    }

    @Override
    protected void init() {

    }
}
