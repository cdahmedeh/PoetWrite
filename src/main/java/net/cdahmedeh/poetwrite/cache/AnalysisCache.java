/**
 * PoetWrite - A Poetry Writing Application
 * Copyright (C) 2025 Ahmed El-Hajjar
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

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import lombok.SneakyThrows;
import net.cdahmedeh.poetwrite.analysis.EntityAnalysis;
import net.cdahmedeh.poetwrite.analysis.PhonemeAnalysis;
import net.cdahmedeh.poetwrite.analysis.RhymeAnalysis;
import net.cdahmedeh.poetwrite.analysis.WordAnalysis;
import net.cdahmedeh.poetwrite.domain.Entity;
import net.cdahmedeh.poetwrite.domain.Word;
import net.cdahmedeh.poetwrite.domain.WordPair;
import org.apache.commons.lang3.tuple.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds a copy of all the computations for poetry-related analysis.
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
 * VERY IMPORTANT: The AnalysisCache is annotated with @Singleton, otherwise,
 *                 Dagger will inject a new instance for every class that
 *                 depends on it.
 *
 * CONVENTION: Only the *Computer classes should have direct access to this
 *             cache.
 *
 * DESIGN: Notice that there's no 'put' mechanism for the various maps. Instead,
 *         an *Analysis is stored in the cache. And then the *Computer classes
 *         will handle feeding the *Analysis classes. So basically, we are
 *         relying on the reference modification pattern. If an analysis doesn't
 *         exist yet, the appropriate get* methods will create a new one.
 *
 * TODO: Switch to Caffeine once we have the performance profiled.
 * TODO: Switch to a Map from Table for the rhymes cache.
 * TODO: Describe the caches as comments.
 *
 * @author Ahmed El-Hajjar
 */
@Singleton
public class AnalysisCache {

    @Inject
    public AnalysisCache() {}

    private Map<AnalysisKey, EntityAnalysis> cache = new ConcurrentHashMap<>();

    @SneakyThrows
    public <E extends Entity, A extends EntityAnalysis> A get(E entity, Class<A> analysisClass) {
        EntityAnalysis analysis = cache.get(new AnalysisKey(entity, analysisClass));

        if (analysis == null) {
            analysis = analysisClass
                    .getConstructor(entity.getClass())
                    .newInstance(entity);
            cache.put(new AnalysisKey(entity, analysisClass), analysis);
        }

        return analysisClass.cast(analysis);
    }

}
