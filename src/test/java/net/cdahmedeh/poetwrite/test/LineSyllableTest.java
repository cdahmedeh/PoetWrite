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

package net.cdahmedeh.poetwrite.test;

import net.cdahmedeh.poetwrite.component.DaggerTestComponent;
import net.cdahmedeh.poetwrite.component.TestComponent;
import net.cdahmedeh.poetwrite.lib.constructor.LineConstructor;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.service.analyzer.LineAnalyzer;
import net.cdahmedeh.poetwrite.ui.async.TaskBus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This is where we test the syllable (per line) counter. The default safe
 * cases aren't that important. Just as a regression catcher.
 *
 * However, for the lines with errors, like orphaned brackets, needs to be
 * tested. And need to match what RSyntaxTextArea does. Which right now, is
 * very forgiving.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LineSyllableTest {
    @Inject
    TaskBus taskBus;

    @Inject
    LineAnalyzer lineAnalyzer;

    @BeforeAll
    void setup() {
        TestComponent component = DaggerTestComponent.create();

        taskBus = component.getTaskBus();
        taskBus.enableTestMode();

        lineAnalyzer = component.getLineAnalyzer();
    }

    @Test
    void testBasicLines() {
        Line sentence01 = LineConstructor.fromText("this is a line");
        assertEquals(4,  lineAnalyzer.get(sentence01).getTotalSyllables());

        Line sentence02 = LineConstructor.fromText("this is a another line");
        assertEquals(7,  lineAnalyzer.get(sentence02).getTotalSyllables());

        Line sentence03 = LineConstructor.fromText("we are going to be strong with the strength of six mighty men");
        assertEquals(15, lineAnalyzer.get(sentence03).getTotalSyllables());

        Line sentence04 = LineConstructor.fromText("he seems to have a lymph node on his neck");
        assertEquals(10, lineAnalyzer.get(sentence04).getTotalSyllables());

        Line sentence05 = LineConstructor.fromText("we want to eat all the food");
        assertEquals(7,  lineAnalyzer.get(sentence05).getTotalSyllables());
    }

    /**
     * To see if it ignores symbols.
     *
     * TODO: Add more non-alphanumeric symbols into the grammar.
     */
    @Test
    void testLinesWithPunctuationAndCases() {
        Line sentence01 = LineConstructor.fromText("This is a line.");
        assertEquals(4,  lineAnalyzer.get(sentence01).getTotalSyllables());

        Line sentence02 = LineConstructor.fromText("The time has come... Let the clock turn back!");
        assertEquals(9,  lineAnalyzer.get(sentence02).getTotalSyllables());

        Line sentence03 = LineConstructor.fromText("It's really annoying to count syllables by hand.");
        assertEquals(13, lineAnalyzer.get(sentence03).getTotalSyllables());

        Line sentence04 = LineConstructor.fromText("One day he will say, we did it.");
        assertEquals(8, lineAnalyzer.get(sentence04).getTotalSyllables());

        Line sentence05 = LineConstructor.fromText("The cat is great, but the dog is not.");
        assertEquals(9,  lineAnalyzer.get(sentence05).getTotalSyllables());
    }

    /**
     * For notes and asides.
     *
     * As mentioned in the documentation, both [Notes] and (Asides) are ignored
     * in the syllable count, and essentially all of the analyses.
     */
    @Test
    void testLinesWithNotesAndAsides() {
        Line sentence01 = LineConstructor.fromText("[Synth Solo]");
        assertEquals(0,  lineAnalyzer.get(sentence01).getTotalSyllables());

        Line sentence02 = LineConstructor.fromText("The time has come... (Let the clock turn back!)");
        assertEquals(4,  lineAnalyzer.get(sentence02).getTotalSyllables());

        Line sentence03 = LineConstructor.fromText("It's (really) annoying to count syllables [by hand].");
        assertEquals(9, lineAnalyzer.get(sentence03).getTotalSyllables());

        Line sentence04 = LineConstructor.fromText("Great job (dude)! You did it. [Sing fast]");
        assertEquals(5, lineAnalyzer.get(sentence04).getTotalSyllables());

        Line sentence05 = LineConstructor.fromText("(The cat is great, but the dog is not.)");
        assertEquals(0,  lineAnalyzer.get(sentence05).getTotalSyllables());
    }

    /**
     * The edge cases for handling things like orphaned brackets.
     *
     * TODO: Disabled because I haven't done much work on ANTLR parser yet.
     *       To be fixed.
     */
    @Disabled("Our error handling is not robust at all.")
    @Test
    void testLinesWithSyntaxError() {
        // If there's no symbol to begin, then it should go all the way to the line.
        // Essentially, orphan brackets are ignored.
        Line sentence01 = LineConstructor.fromText("Synth Solo] with Saxophone");
        assertEquals(5,  lineAnalyzer.get(sentence01).getTotalSyllables());

        // Anything that trails after an opening bracket should be part of the note/aside
        Line sentence02 = LineConstructor.fromText("Well, this one (is broken is great.");
        assertEquals(3,  lineAnalyzer.get(sentence02).getTotalSyllables());
    }
}
