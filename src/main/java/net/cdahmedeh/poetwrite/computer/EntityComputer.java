package net.cdahmedeh.poetwrite.computer;

import net.cdahmedeh.poetwrite.analysis.EntityAnalysis;
import net.cdahmedeh.poetwrite.cache.AnalysisCache;
import net.cdahmedeh.poetwrite.domain.Entity;

import javax.inject.Inject;

public abstract class EntityComputer<E extends Entity, A extends EntityAnalysis> {
    AnalysisCache analysisCache;

    EntityComputer(AnalysisCache analysisCache) {
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
