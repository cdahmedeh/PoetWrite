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

package net.cdahmedeh.poetwrite.engine.mary;

import lombok.SneakyThrows;
import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.datatypes.MaryDataType;
import net.cdahmedeh.poetwrite.constructor.WordConstructor;
import net.cdahmedeh.poetwrite.domain.Word;

import javax.inject.Inject;
import java.util.Locale;

/**
 * @author Ahmed El-Hajjar
 *
 * For words that aren't in the CMU pronunciation dictionary, we need to use a
 * rule-based approach for extracing phonemes. This also deals with the case
 * where the words aren't dictionary words, or neologisms, or proper names.
 * At the end of the day, this isn't a perfect approach.
 *
 * Keep in mind that the CMU dictionary is ideal and much more precise because
 * it was created by humans rather than having heuristics. So this is more of a
 * fallback.
 *
 * There's a bunch of Python libraries that deal with this perfectly, but the
 * core rhetorical analysis in PoetWrite is about syllables and rhymes, so we
 * can't rely on interop, it will be too slow. So I kind of cheated and thought
 * that a text-to-speech engine would be good enough at that.
 *
 * MaryTTS is a pure Java library for doing this, and has outputs that aren't
 * audio which is nice and can extract allophones and phonemes into a nice XML
 * (not that XML is nice) format. And it's quite fast too, once the server is
 * intialized. I'm trying to avoid using it as much as possible because it's
 * still massively slower than the CMU lookup.
 *
 * For example, for "Endothalmic Adaptance", we get this.
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
 * SAMPA is converted to ARPAbet. I explain in the Phoneme entity header on why
 * ARPAbet is the universal phoneme representation.
 */
public class MaryEngine {
    // Config Stuff

    // The default settings for MaryTTS, wondering if this one day could become
    // a configuration option if we start supporting various English dialects.
    public static final Locale MARY_LOCALE = Locale.US;

    // Just picked the noisiest output. I just need the phonemes but with the
    // ALLOPHONES output each phoneme is a node so I don't have to waste my time
    // parsing the phonemes.
    public static final String MARY_OUTPUT_TYPE = MaryDataType.ALLOPHONES.name();

    // Default voice for MaryTTS. It's apparently lightweight.
    public static final String MARY_VOICE = "cmu-slt-hsmm";

    // Containers

    /**
     * Just the magic server for MaryTTS
     */
    private final MaryInterface mary;

    @Inject
    @SneakyThrows
    /**
     * Just loads up the MaryTTS server. Will think about async later.
     */
    /*package*/ MaryEngine() {
        mary = new LocalMaryInterface();

        mary.setLocale(MARY_LOCALE);
        mary.setOutputType(MARY_OUTPUT_TYPE);
        mary.setVoice(MARY_VOICE);
    }

    @SneakyThrows
    public Word getWord(String word) {
        org.w3c.dom.Document maryDoc = mary.generateXML(word);
        return WordConstructor.fromMaryDoc(word, maryDoc);
    }
    /**
     * Counts the number of syllables in the given text.
     *
     * MaryTTS allophones output gives XML with syllables as XML elements. So we
     * just count them! Magic!
     *
     * Ideally, it should be a word and somewhere the words would be split and
     * fed to this. But I'm not doing any sanitaization. in case doing an entire
     * line is faster than word for word.
     */
}
