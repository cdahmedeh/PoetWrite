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

package net.cdahmedeh.poetwrite.query.interfaces;

import net.cdahmedeh.poetwrite.query.steps.RhymeWithQueryStep;

import javax.management.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Just a wrapper for the query parameter. Instead of having a tree being
 * passed everywhere. This just makes things cleaner. Plus some helper methods
 * for batch operations.
 *
 * TODO: Not parametrized correctly.
 */
public class QueryParameters {
    private Map<Class<?>, QueryParameter> map = new HashMap<>();

    public void put(QueryParameter parameter) {
        map.put(parameter.getClass(), parameter);
    }

    public QueryParameter get(Class<?> clazz) {
        return map.get(clazz);
    }

    public boolean has() {
        return map.isEmpty() == false;
    }

    public List<QueryParameter> all() {
        return new ArrayList<>(map.values());
    }

    public static QueryParameters of(QueryParameter parameter) {
        QueryParameters parameters = new QueryParameters();
        parameters.map.put(parameter.getClass(), parameter);
        return parameters;
    }

    public void put(QueryParameters parameters) {
        parameters.all().forEach(this::put);
    }
}
