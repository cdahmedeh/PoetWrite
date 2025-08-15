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

package net.cdahmedeh.poetwrite.constructor;

import net.cdahmedeh.poetwrite.domain.Phoneme;
import net.cdahmedeh.poetwrite.domain.Word;
import net.cdahmedeh.poetwrite.tools.XmlTools;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ahmed El-Hajjar
 *
 * A bunch of methods to build Word entity instances from the common formats
 * that are currently being used.
 *
 * CMU entries and MaryTTS XML allophone outputs.
 *
 * Right now, the current idea is to parse the phonemes while the words are
 * being generated. TODO: Premature optimization.
 *
 * TODO:
 * I feel like the use of these constructors is going to cause confusion. One
 * day I'll consider refactoring once I start doing some async magic.
 */
public class WordConstructor {
    public static final Pattern CMU_ENTRY_PATTERN = Pattern.compile("(.+)\\s\\s(.+)");

    /**
     * Take a CMU entry and builds a Word instance.
     *
     * CMU dictionary has entries that look like this
     * MILLISECONDS  M IH1 L IH0 S EH2 K AH0 N D Z
     * ^^^^^^^^^^^^  ^ ^^^ ^ ^^^ ^ ^^^ ^ ^^^ ^ ^ ^
     *    word              phonemes
     *
     * The convention is that words are in lower case. And this is where the
     * lower-casing is done. The map in CMUEngine assumes this.
     */
    public static Word fromCmuEntry(String entry) {
        Matcher matcher = CMU_ENTRY_PATTERN.matcher(entry);
        matcher.find();

        String entryWord = matcher.group(1);
        String entryPhonemes = matcher.group(2);

        String sanitizedWord = sanitize(entryWord);
        Word word = new Word(sanitizedWord);

        List<Phoneme> phonemes = PhonemeConstructor.fromArpabets(entryPhonemes);
        word.getPhonemes().addAll(phonemes);

        return word;
    }

    /**
     * TODO:
     * Once we start parsing real poems there will more than just making words
     * lowercase like removing symbols and numbers.
     */
    private static String sanitize(String entryWord) {
        return entryWord.toLowerCase();
    }

    /**
     * Takes the MaryTTS XML allophones output to parse it's SAMPA phonemes and
     * turn it in our Word entity.
     *
     * The output looks like this for "Endothalmic Adaptance".
     *
     * <maryxml xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" version="0.5" xml:lang="en-US" xmlns="http://mary.dfki.de/2002/MaryXML">
     *   <p>
     *     <s>
     *       <phrase>
     *         <t accent="L+H*" g2p_method="rules" ph="' E n - d A - T { l - m I k" pos="NNP">
     *           Endothalmic
     *           <syllable accent="L+H*" ph="E n" stress="1">
     *             <ph p="E"/>
     *             <ph p="n"/>
     *           </syllable>
     *           <syllable ph="d A">
     *             <ph p="d"/>
     *             <ph p="A"/>
     *           </syllable>
     *           <syllable ph="T { l">
     *             <ph p="T"/>
     *             <ph p="{"/>
     *             <ph p="l"/>
     *           </syllable>
     *           <syllable ph="m I k">
     *             <ph p="m"/>
     *             <ph p="I"/>
     *             <ph p="k"/>
     *           </syllable>
     *         </t>
     *         <t accent="!H*" g2p_method="rules" ph="' @ - d { p - t @ n s" pos="NN">
     *           Adaptance
     *           <syllable accent="!H*" ph="@" stress="1">
     *             <ph p="@"/>
     *           </syllable>
     *           <syllable ph="d { p">
     *             <ph p="d"/>
     *             <ph p="{"/>
     *             <ph p="p"/>
     *           </syllable>
     *           <syllable ph="t @ n s">
     *             <ph p="t"/>
     *             <ph p="@"/>
     *             <ph p="n"/>
     *             <ph p="s"/>
     *           </syllable>
     *         </t>
     *         <boundary breakindex="5" tone="L-L%"/>
     *       </phrase>
     *     </s>
     *   </p>
     * </maryxml>
     *
     * All I'm doing is just pulling in the ph property in the syllable nodes.
     *
     * I was originally using Jackson to turn the XML into objects, but it seems
     * that MaryTTS outputs can change slightly in terms of schema. Jsoup has
     * really saved me here.
     */
    public static Word fromMaryDoc(String input, org.w3c.dom.Document document) {
        String xmlText = XmlTools.parseXmlDocToString(document);
        Document xmlDoc = XmlTools.parseXmlTextToDocument(xmlText);

        List<String> elements = xmlDoc
                .select("ph")
                .eachAttr("p");

        List<Phoneme> phonemes = new ArrayList<>();

        for (String element : elements) {
            Phoneme phoneme = PhonemeConstructor.fromSampa(element);
            phonemes.add(phoneme);
        }

        Word word = new Word(input);
        word.getPhonemes().addAll(phonemes);

        return word;
    }
}
