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

package net.cdahmedeh.poetwrite.domain;

import org.apache.commons.lang3.tuple.ImmutablePair;

/**
 * A tuple of a word pair. Currently used for the rhyme analysis, which
 * obviously relies on the relationship between two words.
 *
 * This is just quick and dirty right now. Using ImmutablePair from Apache
 * Commons Lang since it takes care of all the equality and hashCode stuff for
 * us.
 *
 * NOTE: This pair is not symmetrical.
 * So WordPair<A, B> is not the same as WordPair<B, A>
 *
 * The official reason is that rhyming relationships can changed depending on
 * order.
 *
 * The unofficial reason is that I'm lazy.
 */
public class WordPair extends ImmutablePair<Word, Word> implements Entity {
    public WordPair(Word wordA, Word wordB) {
        super(wordA, wordB);
    }
}
