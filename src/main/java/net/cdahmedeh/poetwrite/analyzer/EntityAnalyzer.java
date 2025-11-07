package net.cdahmedeh.poetwrite.analyzer;

import net.cdahmedeh.poetwrite.analysis.EntityAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Entity;

public abstract class EntityAnalyzer<E extends Entity, A extends EntityAnalysis> {
    AnalysisCache analysisCache;

    EntityAnalyzer(AnalysisCache analysisCache) {
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
