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
import lombok.Setter;
import net.cdahmedeh.poetwrite.lib.domain.Word;

/**
 * The definition of a word, for the hover tooltip.
 *
 * Keyed on the Word, because a dictionary does not care what line the word is
 * sitting in. The definition of "moon" is the definition of "moon" wherever it
 * turns up in the poem, which means once the cache is behaving this gets
 * computed once and reused for every "moon" in the text.
 *
 * Same shape as PhonemeAnalysis, so AnalysisCache can build it. It looks for a
 * constructor taking the entity, which is what @RequiredArgsConstructor over a
 * single final Word gives us.
 */
@RequiredArgsConstructor
public class WordDefinitionAnalysis extends FeatureAnalysis {
    @Getter
    private final Word word;

    @Getter @Setter
    private String definition = null;

    @Override
    public boolean analyzed() {
        return definition != null;
    }
}
