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

package net.cdahmedeh.poetwrite.tools;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import lombok.SneakyThrows;

/**
 * @author Ahmed El-Hajjar
 *
 * Luckily Jackson makes parsing JSON a breeze. Still follows the traditions
 * of long class names like BasicPolymorphicTypeValidator. So this just shortens
 * up things a bit.
 *
 * And for the record, it's prononced like a Jason and not J-Sawn. Go ask
 * Crawford when he invented the standard. Also GIF is said with a soft-G not
 * a hard-G. And no, the argument that G stands for Graphics isn't apt. That's
 * how CompuServe intended it. I can't believe that Obama had to make a public
 * statement that he pronunced with a hard-G. Because this is an open-source
 * project, I need to be PC so I can't make any political statements. Sorry.
 */
public class JsonTools {
    /**
     * Take an arbitary object and using hopes and dreams, it converts into
     * JSON.
     */
    @SneakyThrows
    public static String toJson(Object object) {
        BasicPolymorphicTypeValidator validator = BasicPolymorphicTypeValidator.builder()
                .allowIfBaseType(Object.class)
                .build();

        ObjectMapper mapper = new ObjectMapper();
        mapper.activateDefaultTyping(
                validator, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);

        String actual = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        return actual;
    }
}
