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

package net.cdahmedeh.poetwrite.analyzer;

import net.cdahmedeh.poetwrite.analysis.FeatureAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Entity;

/**
 * @author Ahmed El-Hajjar
 *
 * This is where the actual logic for the computation is supposed to be done. It
 * handles interaction with the cache and this is the ONLY place where the cache
 * should be manipulated.
 *
 * All the heavy lifting of dealing with the cache is done here. All you need to
 * do is extend this class and implement the analyze method.
 *
 * See the documentation for details on the cache implementation.
 * Poem Analysis Implementation and Cache Design - /docs/entity-architecture.md
 */
public abstract class FeatureAnalyzer<E extends Entity, A extends FeatureAnalysis> {
    AnalysisCache analysisCache;

    FeatureAnalyzer(AnalysisCache analysisCache) {
        this.analysisCache = analysisCache;
    }

    public A get(E entity, Class<A> analysisClass) {
        A analysis = analysisCache.get(entity, analysisClass);

        if (analysis.analyzed() == false) {
            analyze(entity, analysis);
        }

        return analysis;
    }

    /* package */ abstract void analyze(E entity, A analysis);
}
