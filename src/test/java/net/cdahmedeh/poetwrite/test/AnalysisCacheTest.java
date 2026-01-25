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

import net.cdahmedeh.poetwrite.analysis.FeatureAnalysis;
import net.cdahmedeh.poetwrite.analysis.PhonemeAnalysis;
import net.cdahmedeh.poetwrite.analysis.SyllableAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Word;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ahmed El-Hajjar
 *
 * The cache analysis was designed with very specific assumptions that I can see
 * breaking very easily, especially once we get to the point where we run some
 * optimizations.
 *
 * See the documentation for details on the cache implementation.
 * Poem Analysis Implementation and Cache Design - /docs/entity-architecture.md
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AnalysisCacheTest {
    @Test
    void testAnalysisCache() {
        AnalysisCache cache = new AnalysisCache();

        // == CHECK 001 ==
        // Check if first call that creates a new analysis is the same one
        // retrieved on subsequent calls. Using reference comparison.
        //
        // Much of the cache heavily relies on properly working comparisons
        // for the keys.
        Word word1 = new Word("word1");

        SyllableAnalysis analysis1 = cache.get(word1, SyllableAnalysis.class);
        SyllableAnalysis analysis2 = cache.get(word1, SyllableAnalysis.class);

        assertNotNull(analysis1);
        assertNotNull(analysis2);

        assertTrue( analysis1 == analysis2 );

        // == CHECK 002 ==
        // Same analysis should be retrieved if using two different instances
        // of a Word object. Ensure that the key system relies on the content of
        // the Word object rather than the instance.
        // And make sure that there is no collision. Will catch if the equality
        // doesn't work properly.
        SyllableAnalysis analysis3 = cache.get(new Word("word1"), SyllableAnalysis.class);
        SyllableAnalysis analysis4 = cache.get(new Word("word2"), SyllableAnalysis.class);
        SyllableAnalysis analysis5 = cache.get(new Word("word2"), SyllableAnalysis.class);

        assertTrue( analysis1 == analysis3 );
        assertFalse( analysis1 == analysis4 );
        assertFalse( analysis1 == analysis5 );
        assertTrue( analysis4 == analysis5 );

        // == CHECK 003 ==
        // Make sure that an entity can have multiple different types of
        // analyses. And that they don't overwrite each other.
        FeatureAnalysis analysis6 = cache.get(new Word("word3"), SyllableAnalysis.class);
        FeatureAnalysis analysis7 = cache.get(new Word("word3"), PhonemeAnalysis.class);
        FeatureAnalysis analysis8 = cache.get(new Word("word3"), SyllableAnalysis.class);

        assertNotNull(analysis6);
        assertNotNull(analysis7);
        assertNotNull(analysis8);

        assertTrue(analysis6 instanceof SyllableAnalysis);
        assertTrue(analysis7 instanceof PhonemeAnalysis);
        assertTrue(analysis8 instanceof SyllableAnalysis);
    }
}
