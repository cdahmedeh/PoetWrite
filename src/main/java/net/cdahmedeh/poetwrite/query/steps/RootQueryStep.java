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

package net.cdahmedeh.poetwrite.query.steps;

import net.cdahmedeh.poetwrite.query.interfaces.QueryStep;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class RootQueryStep extends QueryStep {

    @Inject
    public RootQueryStep() {
        super("root");
    }

    /**
     * Builds the tree. Blocking -- AutoCompleteTreeHolder calls this from
     * the TaskBus.
     */
    public QueryStep build() {
        return steps(() -> List.of(
                new RhymeWithQueryStep().steps(),
                new MeterQueryStep().steps(),
                new DefinitionsQueryStep().steps(),
                new RelationshipsQueryStep().steps()
        ));
    }
}
