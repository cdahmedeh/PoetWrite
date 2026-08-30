package net.cdahmedeh.poetwrite.query.interfaces;

import lombok.Getter;
import lombok.Setter;

/**
 * A step that takes typed input. Deliberately NOT a QueryParameter: searching
 * is its own concern and will want more than a string later (match mode,
 * result limit, which dictionaries to consult).
 */
public class QuerySearch {
    @Getter @Setter
    private String text = "";
}