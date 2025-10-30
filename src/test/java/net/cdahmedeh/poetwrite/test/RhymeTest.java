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

import net.cdahmedeh.poetwrite.analysis.RhymeAnalysis;
import net.cdahmedeh.poetwrite.component.DaggerTestComponent;
import net.cdahmedeh.poetwrite.component.TestComponent;
import net.cdahmedeh.poetwrite.computer.RhymeComputer;
import net.cdahmedeh.poetwrite.domain.Word;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Obviously, this tests the ability to determine rhyming. Like I said a million
 * times, the comparison doesn't only return true or false, but actually gives
 * the number of syllables in common.
 *
 * Surprisingly, almost all the tests pass. I'm really happy with how robust
 * this turned out to be.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RhymeTest {
    @Inject
    RhymeComputer rhymeComputer;

    @BeforeAll
    void setup() {
        TestComponent component = DaggerTestComponent.create();
        rhymeComputer = component.getRhymeComputer();
    }

    void assertRhymes(int syllables, String inputA, String inputB) {
        Word wordA = new Word(inputA);
        Word wordB = new Word(inputB);

        rhymeComputer.analyze(wordA, wordB);
        RhymeAnalysis analysis = rhymeComputer.getRhyme(wordA, wordB);
        assertEquals(syllables, analysis.getNumberOfRhymeSyllables());
    }

    @Test
    void testBasicRhyming() {
        assertRhymes(1, "cat", "hat");
        assertRhymes(1, "light", "night");
        assertRhymes(1, "stone", "alone");
        assertRhymes(1, "play", "day");
        assertRhymes(1, "go", "snow");
        assertRhymes(1, "hopeless", "darkness");
        assertRhymes(1, "shades", "maids");
        assertRhymes(1, "shine", "line");
        assertRhymes(1, "proclaim", "exclaim");

        assertRhymes(2, "creation", "station");
        assertRhymes(2, "desire", "entire");
        assertRhymes(2, "confusion", "illusion");
        assertRhymes(2, "relation", "vacation");
        assertRhymes(2, "logical", "teleological");
        assertRhymes(2, "magical", "logical");

        assertRhymes(3, "accidental", "coincidental");
        assertRhymes(3, "calibration", "celebration");
        assertRhymes(3, "examination", "determination");
    }

    @Test
    void testNoRhymes() {
        assertRhymes(0, "dancing", "notebook");
        assertRhymes(0, "table", "running");
        assertRhymes(0, "orange", "silver");
        assertRhymes(0, "window", "garden");
        assertRhymes(0, "music", "apple");
        assertRhymes(0, "drive", "fish");
    }

    @Test
    void testSimilarConsonantButDifferentVowels() {
        assertRhymes(0, "moon", "man");
        assertRhymes(0, "bit", "bet");
        assertRhymes(0, "look", "luck");
        assertRhymes(0, "ran", "run");
    }

    @Test
    void testCloseCalls() {
        assertRhymes(3, "irritation", "limitation");
    }

    @Test
    void testRhymingWithNonDictionaryWords() {
        assertRhymes(2, "lastest", "vastest");
        assertRhymes(1, "mostest", "vastest");

        // Fails
        assertRhymes(3, "supertautological", "teleological");
    }

    @Test
    void testRhymingWihAcronyms() {
        assertRhymes(1, "PhD", "sea");

        // Fails
        assertRhymes(1, "BaSC", "personality");
    }
}
