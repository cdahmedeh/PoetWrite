package net.cdahmedeh.poetwrite.query.interfaces;

import java.util.List;

@FunctionalInterface
public interface QueryCommand {
    public List<QueryStep> run(QueryParameters parameters);
}
