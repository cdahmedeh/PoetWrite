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

package net.cdahmedeh.poetwrite.domain;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author Ahmed El-Hajjar
 *
 * Just a container for a single word. The phonemes are pre-computed when the
 * word is created when using PhonemeConstructor and WordConsturctor.
 *
 * There's no sanitization for the word. Right now, the assumption is that it's
 * all lower case letters, and doesn't include any numbers or symbols.
 *
 * This entity will change a lot probably once we get to the point where we do
 * some parsing and represent the poem as an object structure.
 */
@RequiredArgsConstructor
@ToString
public class Word {
    @Getter
    private final String word;

    @Getter
    private final List<Phoneme> phonemes = Lists.newArrayList();
}
