/*
PoetWrite - A Poetry Writing Application
Copyright (C) 2025 Ahmed El-Hajjar

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

grammar PoemLine;

line : ( words | aside | note )+ ;

words : (token)+ ;
token : WORD | PUNCTUATION | SPACE ;
aside : PARANTHESES_OPEN (token)+ PARANTHESES_CLOSE ;
note : BRACKET_OPEN (token)+ BRACKET_CLOSE ;

WORD : [A-Za-z0-9'-]+ ;
PUNCTUATION : [.,!?] ;
SPACE : [ ]+ ;
NEWLINE : [ ]* [\n] ;
BRACKET_OPEN : '[' ;
BRACKET_CLOSE : ']' ;
PARANTHESES_OPEN : '(' ;
PARANTHESES_CLOSE : ')' ;