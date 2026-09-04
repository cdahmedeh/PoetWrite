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

package net.cdahmedeh.poetwrite.ui.event.hover;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.cdahmedeh.poetwrite.lib.analysis.FeatureAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Word;
import net.cdahmedeh.poetwrite.ui.event.interfaces.AppEvent;

/**
 * Various analysis are done for a single word when hovering over it in the
 * editor.
 *
 * For example, the meaning, the part of speech, or information about metering.
 * The requests for getting that information all done at the same time, but
 * they could take a different amount of time. The UI slowly builds the tooltip
 * as this information is collected.
 *
 * I cheated a bit. Rather than have something like DefinitionAnalyzed event
 * or WordDefinitionAnalyzed event, this responds with the particular analysis
 * and the UI takes care of determining which type it is.
 *
 * TODO: Still consider keeping it seperated. It's just that this is so clean
 *      but breaks our convention a little bit. I don't like that the UI
 *      is aware of the analysis type or even has to check for it.
 */
@RequiredArgsConstructor
public class HoverAnalyzedEvent extends AppEvent {
    @Getter
    private final Word word;

    @Getter @Setter
    private FeatureAnalysis analysis;
}
