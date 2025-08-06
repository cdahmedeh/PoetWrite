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

package net.cdahmedeh.poetwrite.domain;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.cdahmedeh.poetwrite.constant.PhonemeConstants;

import java.util.List;

/**
 * @author Ahmed El-Hajjar
 *
 * A phoneme represents a sound in a word.
 *
 * The English language has a mad spelling system, where words aren't written
 * as how are they are pronounced. And even worse, there's no comphrensive
 * pronunciations rules for written words.
 *
 * I swear, sometimes I just want to strangle the poets and writers of English
 * in the middle-ages and before for having constructed such a terrible spelling
 * system.
 *
 * A YouTuber Aaron Alon pokes fun at this with the hypotethical scenario where
 * English was phonetically consistent. I can only dream.
 * https://www.youtube.com/watch?v=A8zWWp0akUU
 *
 * Some stylistic devices in poetry like syllables and rhymes are based on how
 * words sound rather than how they are written. The challenge then becomes
 * determining how the word only based on it's spelling, which you'll realize
 * is quite difficult to do.
 *
 * So I found some inspiration when I came across the CMU pronunciation
 * dictionary. It neatly maps words into a set of phonemes, accurately
 * describing what the word actually sounds like.
 *
 * Of course, not every word is in CMU, so the rest needs to be done
 * heuristically. I've somewhat cheated by taking advantage of a text-to-speech
 * engine. MaryTTS in particular can do non-audio outputs like allophones which
 * generates a list of phonemes for any word based on its own rule-set.
 *
 * CMU transcribes the phonemes into ARPAbet. While MaryTTS outputs them into
 * SAMPA. So I've chosen ARPAbet for the phonemes because it's in CMU. And have
 * MaryTTS SAMPA output into ARPAbet. This means I can use CMU without any
 * processing.
 *
 * Syllable counting is easy, just count the vowels. And rhyming is just
 * matching backwards up until the last matching vowel.
 *
 * Here's two words in ARPAbet
 *
 *                       Matching phonemes = Rhyming
 *                          ||| | ||| || ||| |
 *                          VVV V VVV VV VVV V
 * CALCULATION  K AE2 L K Y AH0 L EY1 SH AH0 N
 * SPECULATION  S P EH2 K Y AH0 L EY1 SH AH0 N
 *                  ^^^     ^^^   ^^^    ^^^
 *                  |||     |||   |||    |||
 *                  Four vowels = 4 Syllables
 *
 * One other interesting thing in the CMU database is that vowels are marked
 * with stresses. I'm wondering if I could use this for calculating meters.
 * Unfortunately, I still haven't found a way for MaryTTS to output them.
 *
 * As a result, these phonemes will be key to many of the analyses that
 * PoetWrite will perform.
 */
@ToString
@RequiredArgsConstructor
public class Phoneme {
    /**
     * The single sound of the phoneme in ARPAbet.
     */
    @Getter
    private final String phone;

    /**
     * The stress of the phoneme. See the Stress enum for details.
     */
    @Getter
    private final Stress stress;

    /**
     * Checks if the phoneme is a vowel. It's really stupid, it just checks if
     * the phone is in a list of hardcoded vowels.
     */
    public boolean isVowel() {
        return PhonemeConstants.VOWELS.contains(phone);
    }
}