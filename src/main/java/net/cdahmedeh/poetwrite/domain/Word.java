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
@ToString
public class Word implements Entity {
    @Getter
    private final String word;

    public Word(String word) {
        this.word = normalize(word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Word) {
            return word.equals(((Word) obj).word);
        }
        return false;
    }

    private String normalize(String word) {
        return word.trim().toLowerCase();
    }
}
