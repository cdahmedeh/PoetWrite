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
import lombok.Setter;
import net.cdahmedeh.poetwrite.lib.domain.Word;

import java.util.List;

/**
 * Other words that rhyme with this one.
 *
 * Not to be confused with RhymeAnalysis, which answers "do these two words
 * rhyme" for a WordPair. This is the other direction: give it one word, get
 * back what else rhymes with it. Different question, different cost, so it
 * gets its own analysis.
 *
 * This is the expensive one in the preview and it always will be. Whatever
 * ends up backing it has to walk a dictionary instead of looking one word up.
 * Which is exactly why it shouldn't be allowed to hold up the definition
 * sitting above it.
 *
 * Keyed on the Word.
 */
public class WordRhymingWordsAnalysis extends FeatureAnalysis<Word> {
    public WordRhymingWordsAnalysis(Word word) {
        super(word);
    }

    // Empty is a perfectly good answer, so this starts empty instead of null.
    // The analyzed flag on FeatureAnalysis is what says whether it ran.
    @Getter @Setter
    private List<String> rhymingWords = List.of();
}
