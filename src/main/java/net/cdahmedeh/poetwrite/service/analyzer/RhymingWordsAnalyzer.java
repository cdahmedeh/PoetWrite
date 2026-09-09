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
import net.cdahmedeh.poetwrite.lib.analysis.WordRhymingWordsAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.tools.SleepTools;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Map;

/**
 * Finds other words that rhyme with a given one.
 *
 * Hard-coded for now, and deliberately the slowest thing in the preview,
 * because the real one is going to be. Answering this properly means comparing
 * the word's ending against a whole dictionary instead of looking one entry
 * up. So it's the piece most worth keeping off the critical path.
 *
 * TODO: RhymeAnalyzer already knows how to decide whether two words rhyme, and
 *       PhonemeAnalyzer feeds it what it needs. The real version of this walks
 *       the CMU dictionary with those two rather than inventing anything new.
 *       That also makes it expensive enough to want caching, which it already
 *       gets for free by being a FeatureAnalysis.
 */
@Draft("Hard-coded rhyme lists until a dictionary walk is wired in")
@Singleton
public class RhymingWordsAnalyzer extends FeatureAnalyzer<Word, WordRhymingWordsAnalysis> {

    // Slowest piece in the preview on purpose, so the fill-in is obvious.
    private static final long FAKE_DICTIONARY_WALK_MILLIS = 1900;

    private static final Map<String, List<String>> DEMO_RHYMES = Map.of(
            "attention", List.of("intention", "invention", "dimension"),
            "devotion", List.of("emotion", "commotion", "promotion"),
            "motion", List.of("ocean", "notion", "potion"),
            "unspeakable", List.of("unbreakable", "unshakeable", "untakeable"),
            "fadeable", List.of("tradeable", "playable", "shadeable"),
            "blackness", List.of("starkness", "darkness", "harshness"),
            "vividness", List.of("liveliness", "timidness", "solidness"),
            "fortress", List.of("buttress", "mattress", "actress"));

    @Inject
    RhymingWordsAnalyzer(AnalysisCache analysisCache, TaskBus taskBus) {
        super(analysisCache, taskBus);
    }

    public WordRhymingWordsAnalysis get(Word word) {
        return get(word, WordRhymingWordsAnalysis.class);
    }

    @Override
    /* package */ void analyze(Word word, WordRhymingWordsAnalysis analysis) {
        SleepTools.safeSleep(FAKE_DICTIONARY_WALK_MILLIS);

        analysis.setRhymingWords(DEMO_RHYMES.getOrDefault(word.getWord(), List.of()));
    }

    @Override
    public String name() {
        return "Rhyming Words Analyzer";
    }

    @Override
    protected void init() {

    }
}
