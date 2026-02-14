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

package net.cdahmedeh.poetwrite.service.cache;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.cdahmedeh.poetwrite.lib.analysis.FeatureAnalysis;
import net.cdahmedeh.poetwrite.lib.domain.Entity;

/**
 * @author Ahmed El-Hajjar
 *
 * The key used in the cache for specifying an entity and the feature analysis
 * we want.
 *
 * See the documentation for details on the cache implementation.
 * Poem Analysis Implementation and Cache Design - /docs/entity-architecture.md
 *
 * TODO: There's no fancy implementation here yet, I don't know what the
 * performance is like yet. So we're just letting Lombok do the job for us.
 */
@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@ToString
public class AnalysisKey<E extends Entity, A extends FeatureAnalysis> {
    private final E entity;

    private final Class<A> type;
}
