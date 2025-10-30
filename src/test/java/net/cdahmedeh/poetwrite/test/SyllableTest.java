/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2025 Ahmed El-Hajjar
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.cdahmedeh.poetwrite.analyzer.SyllableAnalyzer;
import net.cdahmedeh.poetwrite.component.DaggerTestComponent;
import net.cdahmedeh.poetwrite.component.TestComponent;
import net.cdahmedeh.poetwrite.computer.WordComputer;
import net.cdahmedeh.poetwrite.domain.Word;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Ahmed El-Hajjar
 *
 * Testing the ability to count syllables using the engines I've implemented so
 * far.
 *
 * Some of these tests fail because the syllable counting methods I've been
 * using, right now CMU and MaryTTS aren't perfectly accurate. So I'm not going
 * to fudge the numbers just to get these tests to pass.
 *
 * I know some people get squeemish when code is shipped with failing tests,
 * then find other code to contribute to. Or if you have more reliable ways of
 * counting syllables, ship it to me and I'll give you a big hug.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SyllableTest {
    @Inject
    WordComputer wordComputer;

    @Inject
    SyllableAnalyzer analyzer;

    @BeforeAll
    void setup() {
        TestComponent component = DaggerTestComponent.create();

        wordComputer = component.getWordComputer();
        analyzer = component.getSyllableAnalyzer();
    }

    @Test
    void testOneSyllableWords() {
        List<String> inputs = ImmutableList.of("one", "eat", "phone", "twice");

        for (String input : inputs) {
            Word word = new Word(input);
            wordComputer.analyze(word);
            assertEquals(1, analyzer.count(word));
        }
    }

    @Test
    void testHardOneSyllableWords() {
        List<String> inputs = ImmutableList.of("stress", "screeched", "strengths", "gone", "flat");

        for (String input : inputs) {
            Word word = new Word(input);
            wordComputer.analyze(word);
            assertEquals(1, analyzer.count(word));
        }
    }

    @Test
    void testCommonWords() {
        List<String> inputs = ImmutableList.of(
                "the", "be", "to", "of", "and", "a", "in", "that", "have", "I",
                "it", "for", "not", "on", "with", "he", "as", "you", "do", "at");

        for (String input : inputs) {
            Word word = new Word(input);
            wordComputer.analyze(word);
            assertEquals(1, analyzer.count(word));
        }
    }

    @Test
    void testTwoSyllableWords() {
        List<String> inputs = ImmutableList.of(
                "hello", "eaten", "distance", "follow", "courtyard", "engine", "release");

        for (String input : inputs) {
            Word word = new Word(input);
            wordComputer.analyze(word);
            assertEquals(2, analyzer.count(word));
        }
    }

    @Test
    void testMedicationNames() {
        Word lamictalWord = new Word("Lamictal");
        wordComputer.analyze(lamictalWord);
        assertEquals(3, analyzer.count(lamictalWord));

        Word seroquelWord = new Word("Seroquel");
        wordComputer.analyze(seroquelWord);
        assertEquals(3, analyzer.count(seroquelWord));
    }

    @Test
    void testMedicalTerms() {
        Word agranulocytosisWord = new Word("agranulocytosis");
        wordComputer.analyze(agranulocytosisWord);
        assertEquals(7, analyzer.count(agranulocytosisWord));
    }

    @Test
    void testWordsNotInDictionary() {
        Word neologismWord = new Word("agranulocytosis");
        wordComputer.analyze(neologismWord);
        assertEquals(7, analyzer.count(neologismWord));
    }

    @Test
    void testNeologisms() {
        Word supertautologicalWord = new Word("supertautological");
        wordComputer.analyze(supertautologicalWord);
        assertEquals(7, analyzer.count(supertautologicalWord));
    }

    @Test
    void testNonExistingWords() {
        Word endothalmicWord = new Word("endothalmic");
        wordComputer.analyze(endothalmicWord);
        assertEquals(4, analyzer.count(endothalmicWord));

        Word adaptanceWord = new Word("adaptance");
        wordComputer.analyze(adaptanceWord);
        assertEquals(3, analyzer.count(adaptanceWord));

        Word communitiWord = new Word("communiti");
        wordComputer.analyze(communitiWord);
        assertEquals(4, analyzer.count(communitiWord));

        Word hospictacularWord = new Word("hospictacular");
        wordComputer.analyze(hospictacularWord);
        assertEquals(5, analyzer.count(hospictacularWord));
    }
}
