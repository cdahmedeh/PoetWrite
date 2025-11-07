package net.cdahmedeh.poetwrite.test;

import net.cdahmedeh.poetwrite.analysis.EntityAnalysis;
import net.cdahmedeh.poetwrite.analysis.PhonemeAnalysis;
import net.cdahmedeh.poetwrite.analysis.WordAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Word;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;

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

        WordAnalysis analysis1 = cache.get(word1, WordAnalysis.class);
        WordAnalysis analysis2 = cache.get(word1, WordAnalysis.class);

        assertNotNull(analysis1);
        assertNotNull(analysis2);

        assertTrue( analysis1 == analysis2 );

        // == CHECK 002 ==
        // Same analysis should be retrieved if using two different instances
        // of a Word object. Ensure that the key system relies on the content of
        // the Word object rather than the instance.
        // And make sure that there is no collision. Will catch if the equality
        // doesn't work properly.
        WordAnalysis analysis3 = cache.get(new Word("word1"), WordAnalysis.class);
        WordAnalysis analysis4 = cache.get(new Word("word2"), WordAnalysis.class);
        WordAnalysis analysis5 = cache.get(new Word("word2"), WordAnalysis.class);

        assertTrue( analysis1 == analysis3 );
        assertFalse( analysis1 == analysis4 );
        assertFalse( analysis1 == analysis5 );
        assertTrue( analysis4 == analysis5 );

        // == CHECK 003 ==
        // Make sure that an entity can have multiple different types of
        // analyses. And that they don't overwrite each other.
        EntityAnalysis analysis6 = cache.get(new Word("word3"), WordAnalysis.class);
        EntityAnalysis analysis7 = cache.get(new Word("word3"), PhonemeAnalysis.class);
        EntityAnalysis analysis8 = cache.get(new Word("word3"), WordAnalysis.class);

        assertNotNull(analysis6);
        assertNotNull(analysis7);
        assertNotNull(analysis8);

        assertTrue(analysis6 instanceof WordAnalysis);
        assertTrue(analysis7 instanceof PhonemeAnalysis);
        assertTrue(analysis8 instanceof WordAnalysis);
    }
}
