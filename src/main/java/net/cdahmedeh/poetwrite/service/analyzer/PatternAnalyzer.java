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
import net.cdahmedeh.poetwrite.lib.analysis.PatternAnalysis;
import net.cdahmedeh.poetwrite.lib.analysis.PoemSyllablesAnalysis;
import net.cdahmedeh.poetwrite.lib.analysis.RhymeAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.lib.domain.Poem;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.service.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Holds the rhyming pattern of a poem. It's basically a list of letters.
 *
 * A : This is a line in the poem for you
 * B : But you never know when it will strike.
 * A : How dare you ever pretend that you never knew?
 * B : Because honestly, it puts me in a state of fright.
 *
 * Empty lines or ones that don't belong to any pattern are going to just be
 * blank strings.
 *
 * Keep in mind, right now it's very rudimentary. It only takes into account
 * the last syllable of a line. And doesn't take into account partial rhymes.
 *
 * TODO: Tune for pluri-syllabic rhymes.
 * TODO: Add support for partial rhymes.
 *
 * TODO: Several notes sprinkled about whether line per line analysis or for
 *       the whole poem. Has been pretty fast already.
 */
@Singleton
public class PatternAnalyzer extends FeatureAnalyzer<Poem, PatternAnalysis> {
    private final RhymeAnalyzer rhymeAnalyzer;

    @Inject
    public PatternAnalyzer(AnalysisCache analysisCache, TaskBus taskBus, RhymeAnalyzer rhymeAnalyzer) {
        super(analysisCache, taskBus);
        this.rhymeAnalyzer = rhymeAnalyzer;
    }

    /**
     * Analyzes the pattern and puts it into the analysis. The rhymes are
     * currently just lists of letters like mentioned in the root header
     * comment.
     */
    @Override
    void analyze(Poem poem, PatternAnalysis analysis) {
        List<String> scheme = analysis.getPattern();

        // Since we are using the rhyme group as the key, to prevent chaining.
        Map<String, Word> groups = new LinkedHashMap<>();

        // Last time I did this, I just went through each line, and for each
        // line gone through them again. And got something that was O(n^2).
        //
        // We do it backwards, using a map instead. And the key is the letter
        // for the rhyme group instead, and then check for candidates that way.
        // This is instead O(line * pattern)
        //
        // Again, this doesn't support more than one syllable or partial rhymes.
        //
        // TODO: For the millionth time, rhymes are not very sophistacted right
        //       now.
        for (Line line : poem.getLines()) {
            Word lastWord = line.getLastWord();

            if (lastWord == null) {
                scheme.add("");
                continue;
            }

            String letter = null;
            for (Map.Entry<String, Word> group : groups.entrySet()) {
                if (rhymes(group.getValue(), lastWord)) {
                    letter = group.getKey();
                    break;
                }
            }

            if (letter == null) {
                letter = letterFor(groups.size());
                groups.put(letter, lastWord);
            }

            scheme.add(letter);
        }
    }

    public PatternAnalysis get(Poem entity) {
        return get(entity, PatternAnalysis.class);
    }

    @Override
    public String name() {
        return "Poem Syllables Analyzer";
    }

    @Override
    protected void init() {

    }

    /**
     * Just a convinience method to determine if two words rhyme.
     *
     * Remember, that is walking backwards, since RhymeAnalyzer returns rhymes
     * in the order that they are found in the word.
     *
     * TODO: Hard-coded only for one syllable.
     */
    private boolean rhymes(Word wordA, Word wordB) {
        Integer rhymeSyllables = rhymeAnalyzer.get(wordA, wordB).getNumberOfRhymeSyllables();
        return rhymeSyllables != null && rhymeSyllables >= 1;
    }

    /**
     * Converts a number to the equivalent letter.
     *
     * Example:
     *
     * 1 becomes A
     * 3 becomes C
     * 27 becomes AA
     *
     * It's very unlikely that a poem will have more than 26 sounds. But you
     * never know.
     */
    private String letterFor(int index) {
        StringBuilder letter = new StringBuilder();
        index++;
        while (index > 0) {
            index--;
            letter.insert(0, (char) ('A' + index % 26));
            index /= 26;
        }
        return letter.toString();
    }
}
