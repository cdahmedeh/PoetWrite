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
 * Words that mean roughly what this word means.
 *
 * Its own analysis instead of a field on WordDefinitionAnalysis, because a
 * thesaurus and a dictionary are two different lookups that take two different
 * amounts of time. Bundle them and the definition can't show up until the
 * thesaurus has answered too, which is exactly the problem I split the preview
 * up to get rid of.
 *
 * Keyed on the Word. A thesaurus doesn't care where the word shows up.
 *
 * TODO: Synonyms are really per SENSE, not per word. "light" as a noun and
 *       "light" as an adjective have nothing to do with each other. Once
 *       definitions carry senses properly this probably becomes an analysis of
 *       a sense instead.
 */
public class WordSynonymsAnalysis extends FeatureAnalysis<Word> {
    public WordSynonymsAnalysis(Word word) {
        super(word);
    }

    // Empty is a perfectly good answer, so this starts empty instead of null.
    // The analyzed flag on FeatureAnalysis is what says whether it ran.
    @Getter @Setter
    private List<String> synonyms = List.of();
}
