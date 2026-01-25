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

package net.cdahmedeh.poetwrite.analysis;

/**
 * See the documentation for details on the cache implementation.
 * Poem Analysis Implementation and Cache Design - /docs/entity-architecture.md
 *
 * This stores the result of a rhetorical analysis computation.
 *
 * TODO: Add invalidation method.
 */
public abstract class FeatureAnalysis {
    /**
     * Use this to check if the analysis has been computed yet. The simplest
     * way for now, is just to check if the values of the fields are null.
     *
     * @return
     */
    public abstract boolean analyzed();
}
