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

package net.cdahmedeh.poetwrite.engine;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.constructor.PhonemeConstructor;
import net.cdahmedeh.poetwrite.constructor.WordConstructor;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;
import org.apache.commons.io.IOUtils;

import javax.inject.Inject;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ahmed El-Hajjar
 *
 * A bit blank-minded tonight, but IntelliJ keeps trying to read my mind and
 * make comment suggestions. And like the comments just describe what the code
 * does. Well no duh, to know what the code does, you just READ THE CODE!
 *
 * Carnegie Mellon University conconcted this wonderful Pronouncing Dictionary
 * that I'm guessing they used for some speech recognition research. At least
 * some of it seems to be done by hand.
 *
 * http://www.speech.cs.cmu.edu/cgi-bin/cmudict
 *
 * The database is actually quite neat, especially since it produces accurate
 * pronunciation for some of the more difficult words. Words like 'strengths'
 * are just one syllable, and CMU database has this correctly laid out.
 * MaryTTS fails on this for example.
 *
 * Syllable counting is easy, just count the vowels. And rhyming is just
 * matching backwards up until the last matching vowel.
 *
 * Here's two words.
 * We get these wonderful phonemes in the ARBAbet format.
 *
 *                     Matching phonemes = Rhyming
 *                      | | ||| | ||| || ||| |
 *                      V V VVV V VVV VV VVV V
 * CALCULATION  K AE2 L K Y AH0 L EY1 SH AH0 N
 * SPECULATION  S P EH2 K Y AH0 L EY1 SH AH0 N
 *                  ^^^     ^^^   ^^^    ^^^
 *                  |||     |||   |||    |||
 *                  Four vowels = 4 Syllables
 *
 * TODO:
 * It gives these numbers for the vowels for the stresses on the syllables, I
 * wonder if it could be used for analyzing meters. If I find out I get
 * something out of MaryTTS that is similar, then this will be a no-brainer.
 *
 */
public class CmuEngine {
    public static final String CMUDICT_FILE = "dicts/cmudict-0.7b";

    public static final Pattern CMU_ENTRY_PATTERN = Pattern.compile("(.+)\\s\\s(.+)");

    // Containers

    /**
     * Just maps the word to their phonemes verbatim from CMU file.
     * Something like
     * { "calculation" , "K AE2 L K Y AH0 L EY1 SH AH0 N" }
     * { "speculation" , "S P EH2 K Y AH0 L EY1 SH AH0 N" }
     */
    // The lower case is in WordContrustor
    private final Map<String, List<Phoneme>> cmuMap = Maps.newHashMap();

    @Inject
    @SneakyThrows
        /*package*/ CmuEngine() {
        InputStream stream = ClassLoader.getSystemResourceAsStream(CMUDICT_FILE);
        List<String> entries = IOUtils.readLines(stream, Charset.defaultCharset());

        for (String entry : entries) {
            if (isCommentEntry(entry)) {
                continue;
            }

            List<Phoneme> phonemes = fromCmuEntry(entry);
        }
    }

    public List<Phoneme> fromCmuEntry(String entry) {
        Matcher matcher = CMU_ENTRY_PATTERN.matcher(entry);
        matcher.find();

        String entryWord = matcher.group(1);
        String entryPhonemes = matcher.group(2);

        List<Phoneme> phonemes = PhonemeConstructor.fromArpabets(entryPhonemes);

        cmuMap.put(entryWord.toLowerCase(), phonemes);

        return phonemes;
    }

    public List<Phoneme> getWord(Word word) {
        return cmuMap.get(word.getWord());
    }

    /**
     * This just checks if the word is in CMU. Just to know when to fallback to
     * other methods.
     */
    public boolean hasWord(String word) {
        return cmuMap.containsKey(word);
    }

    /**
     * Checks if the line in the CMU file is a comment so that it can be
     * ignored.
     *
     * Just for some syntaxic sugar to make the code more readable.
     */
    private boolean isCommentEntry(String entry) {
        return entry.startsWith(";;;");
    }
}