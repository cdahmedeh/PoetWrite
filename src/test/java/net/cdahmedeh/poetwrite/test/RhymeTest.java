/**
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

import net.cdahmedeh.poetwrite.analyzer.RhymeAnalyzer;
import net.cdahmedeh.poetwrite.component.DaggerRhymeAnalyzerComponent;
import net.cdahmedeh.poetwrite.component.RhymeAnalyzerComponent;
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
    RhymeAnalyzer analyzer;

    @BeforeAll
    void setup() {
        RhymeAnalyzerComponent component = DaggerRhymeAnalyzerComponent.create();
        analyzer = component.getRhymeAnalyzer();
    }

    @Test
    void testBasicRhyming() {
        assertEquals(1, analyzer.compare("cat", "hat"));
        assertEquals(1, analyzer.compare("light", "night"));
        assertEquals(1, analyzer.compare("stone", "alone"));
        assertEquals(1, analyzer.compare("play", "day"));
        assertEquals(1, analyzer.compare("go", "snow"));
        assertEquals(1, analyzer.compare("hopeless", "darkness"));
        assertEquals(1, analyzer.compare("shades", "maids"));
        assertEquals(1, analyzer.compare("shine", "line"));
        assertEquals(1, analyzer.compare("proclaim", "exclaim"));

        assertEquals(2, analyzer.compare("creation", "station"));
        assertEquals(2, analyzer.compare("desire", "entire"));
        assertEquals(2, analyzer.compare("confusion", "illusion"));
        assertEquals(2, analyzer.compare("relation", "vacation"));
        assertEquals(2, analyzer.compare("logical", "teleological"));
        assertEquals(2, analyzer.compare("magical", "logical"));

        assertEquals(3, analyzer.compare("accidental", "coincidental"));
        assertEquals(3, analyzer.compare("calibration", "celebration"));
        assertEquals(3, analyzer.compare("examination", "determination"));
    }

    @Test
    void testNoRhymes() {
        assertEquals(0, analyzer.compare("dancing", "notebook"));
        assertEquals(0, analyzer.compare("table", "running"));
        assertEquals(0, analyzer.compare("orange", "silver"));
        assertEquals(0, analyzer.compare("window", "garden"));
        assertEquals(0, analyzer.compare("music", "apple"));
        assertEquals(0, analyzer.compare("drive", "fish"));
    }

    @Test
    void testSimilarConsonantButDifferentVowels() {
        assertEquals(0, analyzer.compare("moon", "man"));
        assertEquals(0, analyzer.compare("bit", "bet"));
        assertEquals(0, analyzer.compare("look", "luck"));
        assertEquals(0, analyzer.compare("ran", "run"));
    }

    @Test
    void testCloseCalls() {
        assertEquals(3, analyzer.compare("irritation", "limitation"));
    }

    @Test
    void testRhymingWithNonDictionaryWords() {
        assertEquals(2, analyzer.compare("lastest", "vastest"));
        assertEquals(1, analyzer.compare("mostest", "vastest"));
        assertEquals(3, analyzer.compare("supertautological", "teleological"));
    }
}
