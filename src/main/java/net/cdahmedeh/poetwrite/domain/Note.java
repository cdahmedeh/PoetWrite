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

/**
 * @author Ahmed El-Hajjar
 *
 * This is just a comment that can be inserted into the text. It is completely
 * ignored by PoetWrite.
 *
 * This is another poem [fill in this with a word] about food.
 * (Inspired by Weird Al's extensive discography about food)
 *
 * This is still an early design. See the comments in Line.
 *
 * See ./docs/poem-syntax-and-entity-structure.md for a more complete view.
 */
package net.cdahmedeh.poetwrite.domain;

public class Note extends Node{
    public Note(String text) {
        super(text);
    }
}
