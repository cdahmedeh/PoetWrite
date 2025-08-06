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

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * @author Ahmed El-Hajjar
 *
 * This represents the Stress of a phoneme. Right now, it is only provided for
 * words that are found in CMU.
 *
 * NOT_AVAILABLE either means that the phoneme doesn't have a stress because
 * it's not a vowel, or because it's not present (such as in the case of
 * MaryTTS).
 */
@RequiredArgsConstructor
@ToString
public enum Stress {
    NO_STRESS     ("0"),
    PRIMARY       ("1"),
    SECONDARY     ("2"),
    NOT_AVAILABLE ("");

    private final String value;

    public static Stress from(String stress) {
        switch (stress) {
            case "0":
                return NO_STRESS;
            case "1":
                return PRIMARY;
            case "2":
                return SECONDARY;
            default:
                return NOT_AVAILABLE;
        }
    }
}
