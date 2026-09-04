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

package net.cdahmedeh.poetwrite.lib.analysis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.cdahmedeh.poetwrite.lib.domain.Line;
import net.cdahmedeh.poetwrite.lib.domain.Word;

import java.util.Map;

/**
 * The part of speech of every word in a line. Is it a very, a noun, an article,
 * etc?
 *
 * TODO: For now, I'm calling the part-of-speech components as tags. But they
 *       should become an enum of the different types.
 *
 * TODO: I'm cheating so badly here. Rather than have the word be the key, it's
 *       actually the character position of the word. And then, we do the
 *       computation.
 *
 * TODO: All hard-coded.
 */
@RequiredArgsConstructor
public class LinePartOfSpeechAnalysis extends FeatureAnalysis {
    @Getter
    private final Line line;

    // Word start offset, to its tag. Null until the analyzer has filled it in,
    // which is also how analyzed() knows it has run. An empty map is a valid
    // answer, a line with no words in it has no tags.
    @Getter
    private Map<Integer, String> tags = null;

    public void setTags(Map<Integer, String> tags) {
        this.tags = tags;
    }

    /**
     * The tag for one word of the line, or null if there is nothing for it.
     */
    public String getTag(Word word) {
        return tags == null ? null : tags.get(word.getStart());
    }

    @Override
    public boolean analyzed() {
        return tags != null;
    }
}
