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

package net.cdahmedeh.poetwrite.constructor;

import net.cdahmedeh.poetwrite.constant.PhonemeConstants;
import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Stress;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Helper methods to build Phoneme entries for my common use cases. Namely
 * turning the APRBbat and SAMPA phonemes into the entity.
 *
 * In the Phoneme class header comment, I explain why I've chosen ARBApet as the
 * common phoneme format.
 */
public class PhonemeConstructor {
    /**
     * The regex pattern for a phoneme in ARBApet format. It's basically a set
     * of characters to represent a sound, and then possibly a number after
     * some vowels to indicate stress.
     *
     * A set looks like this
     * CALCULATION  K AE2 L K Y AH0 L EY1 SH AH0 N
     */
    public static final Pattern PHONEME_PATTERN = Pattern.compile("([A-Z]+)(\\d?)");

    /**
     * If I called these SPACE_REGEX, it would become a meme.
     *
     * You know
     * const ONE = "1"
     * const TWO = "2"
     * ...
     */
    public static final String ARPABETS_SEPERATOR_REGEX = "\\s";

    /**
     * This probably has to go since I don't use fromArpabets(..) at all.
     */
    public static final String SAMPAS_SEPERATOR_REGEX = "\\s";

    /**
     * This builds a phoneme instance from a single ARPAbet symbol.
     */
    public static final Phoneme fromArpabet(String symbol) {
        Matcher matcher = PHONEME_PATTERN.matcher(symbol);
        matcher.find();

        String croppedPhone = matcher.group(1);
        String stress = matcher.group(2);

        Phoneme phoneme = new Phoneme(croppedPhone, Stress.from(stress));
        return phoneme;
    }

    /**
     * Builds a list of phonemes from a string of ARPAbet symbols that are space
     * seperated.
     */
    public static final List<Phoneme> fromArpabets(String arpabets) {
        String[] symbols = arpabets.split(ARPABETS_SEPERATOR_REGEX);

        List<Phoneme> phonemes = List.of(symbols)
                .stream()
                .map(PhonemeConstructor::fromArpabet)
                .toList();
        return phonemes;
    }

    /**
     * This builds a phoneme instance from a single SAMPA symbol, and converts
     * it to ARPAbet.
     */
    public static final Phoneme fromSampa(String symbol) {
        String converted = PhonemeConstants.SAMPA_TO_ARPABET_CONVERSION.get(symbol);

        Phoneme phoneme = new Phoneme(converted, Stress.NOT_AVAILABLE);
        return phoneme;
    }

    /**
     * Builds a list of phonemes from a string of SAMPA symbols that are space
     * seperated.
     *
     * Not used at all. Just here for completeness. Static-code analysis tools
     * can go to hell.
     */
    public static final List<Phoneme> fromSampas(String sampas) {
        String[] symbols = sampas.split(SAMPAS_SEPERATOR_REGEX);

        List<Phoneme> phonemes = List.of(symbols)
                .stream()
                .map(PhonemeConstructor::fromSampa)
                .toList();
        return phonemes;
    }
}
