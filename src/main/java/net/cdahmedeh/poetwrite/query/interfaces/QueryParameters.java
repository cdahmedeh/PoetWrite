package net.cdahmedeh.poetwrite.query.interfaces;

import net.cdahmedeh.poetwrite.query.steps.RhymeWithQueryStep;

import javax.management.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

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
