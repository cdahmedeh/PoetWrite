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

import net.cdahmedeh.poetwrite.analyzer.SyllableAnalyzer;
import net.cdahmedeh.poetwrite.component.DaggerSyllableAnalyzerComponent;
import net.cdahmedeh.poetwrite.component.SyllableAnalyzerComponent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;

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
    SyllableAnalyzer analyzer;

    @BeforeAll
    void setup() {
        SyllableAnalyzerComponent component = DaggerSyllableAnalyzerComponent.create();
        analyzer = component.getSyllableAnalyzer();
    }

    @Test
    void testOneSyllableWords() {
        assertEquals(1, analyzer.count("one"));
        assertEquals(1, analyzer.count("eat"));
        assertEquals(1, analyzer.count("phone"));
        assertEquals(1, analyzer.count("twice"));
    }

    @Test
    void testHardOneSyllableWords() {
        assertEquals(1, analyzer.count("stress"));
        assertEquals(1, analyzer.count("screeched"));
        assertEquals(1, analyzer.count("strengths"));
        assertEquals(1, analyzer.count("gone"));
        assertEquals(1, analyzer.count("flat"));
    }

    @Test
    void testCommonWords() {
        assertEquals(1, analyzer.count("the"));
        assertEquals(1, analyzer.count("be"));
        assertEquals(1, analyzer.count("to"));
        assertEquals(1, analyzer.count("of"));
        assertEquals(1, analyzer.count("and"));
        assertEquals(1, analyzer.count("a"));
        assertEquals(1, analyzer.count("in"));
        assertEquals(1, analyzer.count("that"));
        assertEquals(1, analyzer.count("have"));
        assertEquals(1, analyzer.count("I"));
        assertEquals(1, analyzer.count("it"));
        assertEquals(1, analyzer.count("for"));
        assertEquals(1, analyzer.count("not"));
        assertEquals(1, analyzer.count("on"));
        assertEquals(1, analyzer.count("with"));
        assertEquals(1, analyzer.count("he"));
        assertEquals(1, analyzer.count("as"));
        assertEquals(1, analyzer.count("you"));
        assertEquals(1, analyzer.count("do"));
        assertEquals(1, analyzer.count("at"));
    }

    @Test
    void testTwoSyllableWords() {
        assertEquals(2, analyzer.count("hello"));
        assertEquals(2, analyzer.count("eaten"));
        assertEquals(2, analyzer.count("distance"));
        assertEquals(2, analyzer.count("follow"));
        assertEquals(2, analyzer.count("courtyard"));
        assertEquals(2, analyzer.count("engine"));
        assertEquals(2, analyzer.count("release"));
    }

    @Test
    void testMedicationNames() {
        assertEquals(3, analyzer.count("Lamictal"));
        assertEquals(3, analyzer.count("Seroquel"));
    }

    @Test
    void testMedicalTerms() {
        assertEquals(7, analyzer.count("agranulocytosis"));
    }

    @Test
    void testWordsNotInDictionary() {
        assertEquals(4, analyzer.count("neologism"));
    }

    @Test
    void testNeologisms() {
        assertEquals(7, analyzer.count("supertautological"));
    }

    @Test
    void testNonExistingWords() {
        assertEquals(4, analyzer.count("endothalmic"));
        assertEquals(3, analyzer.count("adaptance"));
        assertEquals(4, analyzer.count("communiti"));
        assertEquals(5, analyzer.count("hospictacular"));
    }
}
