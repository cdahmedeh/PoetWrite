package net.cdahmedeh.poetwrite.cache;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.cdahmedeh.poetwrite.analysis.EntityAnalysis;
import net.cdahmedeh.poetwrite.domain.Entity;

@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class AnalysisKey<E extends Entity, A extends EntityAnalysis> {
    private final E entity;

    private final Class<A> type;
}
