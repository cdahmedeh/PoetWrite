package net.cdahmedeh.poetwrite.domain;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class WordPair extends ImmutablePair<Word, Word> implements Entity {
    public WordPair(Word wordA, Word wordB) {
        super(wordA, wordB);
    }
}
