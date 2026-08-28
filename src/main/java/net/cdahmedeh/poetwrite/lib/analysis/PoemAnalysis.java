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
import net.cdahmedeh.poetwrite.lib.domain.Poem;
import net.cdahmedeh.poetwrite.service.analyzer.FeatureAnalyzer;

import javax.inject.Inject;

/**
 * Previously, the Poem was parsed from a static helper method, but this caused
 * issues when I was implementing the Autocomplete Wizard, since it doesn't
 * have access to the Poem itself.
 *
 * I kept the input poem and output as a parsed poem seperate. I could in
 * theory replace the initial poem, or at least re-parse it, but then it causes
 * all sorts of complications when it's being used.
 */
public class PoemAnalysis extends FeatureAnalysis {
    @Getter
    private final Poem poem;

    @Getter @Setter
    private Poem parsed;

    @Inject
    public PoemAnalysis(Poem poem) {
        this.poem = poem;
    }

    @Override
    public boolean analyzed() {
        return parsed != null;
    }
}
