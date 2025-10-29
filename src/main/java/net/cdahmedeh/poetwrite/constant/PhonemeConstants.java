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

package net.cdahmedeh.poetwrite.constant;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * @author Ahmed El-Hajjar
 *
 * This is for making dealing with ARPAbet and SAMPA a little bit more simple.
 */
public class PhonemeConstants {
    /**
     * The Phoneme entity uses this list to check if the phoneme is a vowel.
     */
    public static final List<String> VOWELS = ImmutableList.of(
            "AA",
            "AE",
            "AH",
            "AO",
            "AW",
            "AY",
            "EH",
            "ER",
            "EY",
            "IH",
            "IY",
            "OW",
            "OY",
            "UH",
            "UW"
    );

    /**
     * The conversion table from SAMPA to ARPAbet.
     *
     * One issue was the MaryTTS doesn't follow the SAMPA convention perfectly.
     * I annotated the entries that are different from the convention as
     * outputted by MaryTTS.
     *
     * References:
     * - https://ufal.mff.cuni.cz/~odusek/courses/npfl123/data/arpabet_to_sampa.html
     * - https://chatgpt.com/s/t_6892b04309808191a85e52fbcafb9cc4
     */
    public static final Map<String, String> SAMPA_TO_ARPABET_CONVERSION = Map.ofEntries(
            // Vowels
            entry("A", "AA"),   // removed :
            entry("{",  "AE"),
            entry("V",  "AH"),  // schwa often "@" in SAMPA; see below
            entry("@",  "AH"),  // consider mapping to AX/AH0 depending on your stress handling
            entry("Q",  "AO"),  // Not sure if this is a typo.
            entry("O",  "AO"),  // Not in conversion tables.
            entry("aU", "AW"),
            entry("AI", "AY"),  // actually aI
            entry("E",  "EH"),  // actually e
            entry("EI", "EY"),  // actually eI
            entry("I",  "IH"),
            entry("i", "IY"),   // removed :
            entry("@U", "OW"),
            entry("OI", "OY"),
            entry("U",  "UH"),
            entry("u", "UW"),   // removed :

            // Consonants
            entry("b",  "B"),
            entry("tS", "CH"),
            entry("d",  "D"),
            entry("D",  "DH"),
            entry("f",  "F"),
            entry("g",  "G"),
            entry("h",  "HH"),
            entry("dZ", "JH"),
            entry("k",  "K"),
            entry("l",  "L"),
            entry("m",  "M"),
            entry("n",  "N"),
            entry("N",  "NG"),
            entry("p",  "P"),
            entry("r\\", "R"),   // Java string for SAMPA r\ is "r\\"
            entry("r=", "ER"),   // What not in any conversion table.
            entry("s",  "S"),
            entry("S",  "SH"),
            entry("t",  "T"),
            entry("T",  "TH"),
            entry("v",  "V"),
            entry("w",  "W"),
            entry("j",  "Y"),
            entry("z",  "Z"),
            entry("Z",  "ZH")
    );
}
