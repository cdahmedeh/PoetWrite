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

package net.cdahmedeh.poetwrite.cache;

import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.analysis.FeatureAnalysis;
import net.cdahmedeh.poetwrite.domain.Entity;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds a copy of all the computations for poetry-related analysis.
 *
 * See the documentation for details on the cache implementation.
 * Poem Analysis Implementation and Cache Design - /docs/entity-architecture.md
 *
 * Since texts tend to change slowly, and many computations are done over and
 * over again, it makes sense to cache the results of said computations.
 * Avoiding oft-repeated computations will really speed things up. Some other
 * upcoming computations will be resource-intensive such as pattern-detection,
 * dictionary lookups and so on.
 *
 * The entity objects have been kept as light-weight as possible. It was really
 * tempting to have the computations done within fields in the entity objects.
 * However, for simplicity, I'll be rebuilding the entity structure after every
 * parsing pass, because maintaining their state and monitoring minute changes
 * will be a nightmare. This part will be really fast, ANTLR does a good job
 * with its visitor pattern.
 *
 * Instead, as an optimization, the actual analyses are computed and stored in
 * its own *Analysis object. And we keep a cache of that. So, these analyses
 * are stored upon computation in a basic key-value map. And to retrieve them,
 * the entity objects become the keys essentially.
 *
 * The *Analysis objects have a very rudimentary invalidation mechanism, where
 * the fields that need to be computed for the first time, or re-computed, are
 * just nulled.
 *
 * This may seem a bit of an early optimization. But, I've done to avoid having
 * to do a massive refactor when our future-set grows. And I think it's safe
 * for now.
 *
 * CACHE DATA STRUCTURE:
 * The cache is a conventional map that approximates the ability to have one
 * entity associated to multiple analyses. This simulates the behavior of a
 * multimap where one key has multiple values.
 *
 * To do this, the association is done via AnalysisKey, which holds the entity
 * and the feature type. Then that's what you use to get a specific feature
 * for an entity.
 *
 * LIMITATIONS:
 * This will probably be important in the future, but right now, there's no way
 * to know how many features have been analyzed for a given entity. This makes
 * things like invalidation a bit tricky.
 *
 * VERY IMPORTANT: The AnalysisCache is annotated with @Singleton, otherwise,
 *                 Dagger will inject a new instance for every class that
 *                 depends on it.
 *
 * CONVENTION: Only the *Analyzer classes should have direct access to this
 *             cache.
 *
 * DESIGN: Notice that there's no 'put' mechanism for the various maps. Instead,
 *         an *Analyzer is stored in the cache. And then the *Analyzer classes
 *         will handle feeding the *Analysis classes. So basically, we are
 *         relying on the reference modification pattern. If an analysis doesn't
 *         exist yet, the appropriate get* methods will create a new one.
 *
 * TODO: Switch to Caffeine once we have the performance profiled.
 *
 * SUBTLETIES:
 * Java does generic-type erasure since it's a compile-time-only
 * feature. So at runtime, we have no generic type information, so
 * some clever reflecttion is done here.
 *
 * So
 * analysis = analysisClass
 *     .getConstructor(entity.getClass())
 *     .newInstance(entity);
 * return analysisClass.cast(analysis);
 *
 * Is basically like
 * FeatureAnalysis analysis = new A(entity);
 * return analysis;
 *
 * This assumes that the constructor of the implementation of the
 * FeatureAnalysis class has a constructor with a single parameter
 * of type Entity.
 *
 * @author Ahmed El-Hajjar
 */
@Singleton
public class AnalysisCache {

    @Inject
    public AnalysisCache() {}

    private Map<AnalysisKey, FeatureAnalysis> cache = new ConcurrentHashMap<>();

    @SneakyThrows
    public <E extends Entity, A extends FeatureAnalysis> A get(E entity, Class<A> analysisClass) {
        AnalysisKey<E, A> key = AnalysisKey.of(entity, analysisClass);
        FeatureAnalysis analysis = cache.get(key);

        if (analysis == null) {
            analysis = analysisClass
                    .getConstructor(entity.getClass())
                    .newInstance(entity);

            cache.put(key, analysis);
        }

        return analysisClass.cast(analysis);
    }
}
