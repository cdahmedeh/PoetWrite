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

import net.cdahmedeh.poetwrite.analyzer.SyllableAnalyzer;
import net.cdahmedeh.poetwrite.component.DaggerSyllableAnalyzerComponent;
import net.cdahmedeh.poetwrite.component.SyllableAnalyzerComponent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
