package net.cdahmedeh.poetwrite.domain;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class Poem {
    @Getter
    private final List<Line> lines = Lists.newArrayList();
}
