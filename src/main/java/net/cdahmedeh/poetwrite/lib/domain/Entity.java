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

package net.cdahmedeh.poetwrite.lib.domain;

/**
 * All domain objects are considered Entity. The most important one being Word
 * as it contains the majority of what is needed to do the external analyses.
 *
 * This is the main key for the cache, and used to pull in the various
 * computations. Remember, these objects are as skinny as possible. The results
 * of computations should NOT be stored here. They go into the associated
 * FeatureAnalysis implementations.
 *
 * These have been designed to be completely disposable. Parsing the text into
 * this is very quick, so we can do it over and over again, and let the cache
 * be the window to the actual analyses.
 *
 * You can find how this is all designed in the following documentation.
 * Poem Syntax and Domain Structure - /docs/poem-syntax-and-domain-structure.md
 *
 * See the documentation for details on the cache implementation.
 * Poem Analysis Implementation and Cache Design - /docs/entity-architecture.md
 */
public interface Entity {
}
