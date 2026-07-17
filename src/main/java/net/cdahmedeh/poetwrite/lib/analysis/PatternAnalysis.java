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
import net.cdahmedeh.poetwrite.lib.domain.Poem;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the rhyming pattern of a poem. It's basically a list of letters.
 *
 * A : This is a line in the poem for you
 * B : But you never know when it will strike.
 * A : How dare you ever pretend that you never knew?
 * B : Because honestly, it puts me in a state of freight.
 *
 * Empty lines or ones that don't belong to any pattern are going to just be
 * blank strings.
 */
@RequiredArgsConstructor
public class PatternAnalysis extends FeatureAnalysis {
    @Getter
    private final Poem poem;

    @Getter
    List<String> pattern = new ArrayList<>();

    @Override
    public boolean analyzed() {
        return pattern.isEmpty() == false;
    }
}
