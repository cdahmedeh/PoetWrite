/**
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

import lombok.SneakyThrows;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.io.Writer;

/**
 * @author Ahmed El-Hajjar
 *
 * Just some methods to hide the ugliness of the various DOM and XML APIs that
 * come with the JDK. What was it again? DocumentFactoryBuilderParserGenerator?
 *
 * Also some stuff for dealing with external libraries that do XML and DOM
 * stuff. And for hiding the ugliness that both Jsoup and w3c have the same
 * type Document.
 *
 * Fun tip. If you do clever static imports, it almost looks like Python code!
 * With stuff like this, you can hide all the boilerplate!
 */
public class XmlTools {
    /**
     * Takens in a DOM Document that normally comes from libraries that output
     * XML. Most used for MarryTTS that outputs XML in the w3c format. Spits out
     * a pretty XML as a fancy string.
     */
    @SneakyThrows
    public static String parseXmlDocToString(org.w3c.dom.Document document) {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        Writer writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));

        return writer.toString();
    }

    /**
     * Uses Jsoup to parse an XML document as text into a Document. Keep in mind
     * that org.w3m.doc.Document and org.jsoup.nodes.Document are different,
     * so be careful with your imputs.
     */
    public static Document parseXmlTextToDocument(String xmlText) {
        return Jsoup.parse(xmlText);
    }
}
