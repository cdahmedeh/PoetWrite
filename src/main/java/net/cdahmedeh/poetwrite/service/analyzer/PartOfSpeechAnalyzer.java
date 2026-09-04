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
import net.cdahmedeh.poetwrite.lib.analysis.LinePartOfSpeechAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.tools.SleepTools;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tags the words of a line with their part of speech.
 *
 * Takes a Line rather than a Word on purpose, see LinePartOfSpeechAnalysis.
 * You can tell a part-of-speech from the word alone. So you need the whole
 * line.
 *
 * TODO: This is all hardcoded, like I said before, the metering system has
 *       not been fully flesched out.
 */
@Draft("Fake tagger until a real one is wired in")
@Singleton
public class PartOfSpeechAnalyzer extends FeatureAnalyzer<Line, LinePartOfSpeechAnalysis> {

    private static final long FAKE_TAGGER_MILLIS = 1600;

    private static final List<String> DETERMINERS =
            List.of("the", "a", "an", "this", "that", "these", "those", "my", "your", "our");

    private static final List<String> PRONOUNS =
            List.of("i", "we", "you", "he", "she", "it", "they", "me", "us", "him", "her", "them");

    @Inject
    PartOfSpeechAnalyzer(AnalysisCache analysisCache, TaskBus taskBus) {
        super(analysisCache, taskBus);
    }

    public LinePartOfSpeechAnalysis get(Line line) {
        return get(line, LinePartOfSpeechAnalysis.class);
    }

    @Override
    /* package */ void analyze(Line line, LinePartOfSpeechAnalysis analysis) {
        SleepTools.safeSleep(FAKE_TAGGER_MILLIS);

        Map<Integer, String> tags = new HashMap<>();

        Word previous = null;
        for (Word word : line.getAllWords()) {
            tags.put(word.getStart(), tag(word, previous));
            previous = word;
        }

        analysis.setTags(tags);
    }

    private String tag(Word word, Word previous) {
        if (PRONOUNS.contains(word.getWord())) {
            return "pronoun";
        }
        if (DETERMINERS.contains(word.getWord())) {
            return "determiner";
        }
        if (previous != null && DETERMINERS.contains(previous.getWord())) {
            return "noun";
        }
        if (previous != null && PRONOUNS.contains(previous.getWord())) {
            return "verb";
        }
        return "noun (assumed)";
    }

    @Override
    public String name() {
        return "Part of Speech Analyzer";
    }

    @Override
    protected void init() {

    }
}
