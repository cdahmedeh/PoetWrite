# PoetWrite Rhetorical Analysis Basics

# Preamble

Most poetry rhetorical features are based on how words sound rather than spelling. The most obvious one being rhyming.

And English doesn't exactly make this an intuitive thing to do. The spelling system is a mad especially since there's no consistent pronunciation rules for written words.

Working a solution for this really got on my nerves and developed a big hate for the poets and writers of English in the middle-ages for having constructed something so convoluted.

Aaron Alon, made a neat video [_"What If English Were Phonetically Consistent"_](https://www.youtube.com/watch?v=A8zWWp0akUU) which pokes some good fun at this problem. A heavily recommended watch.

So what came to mind right away is how text-to-speech engines work, since they sound pretty good these days. It seems that they use some kind of dictionary for most words, and some heuristic-based rules for the rest.

# Determining Pronunciation

CMU was working on some speech-recognition system and altruistically developed an open-source dictionary to map words to a set of phonemes. 

❗ **Phonemes** or _phones_ are units of sound like a consonant or a vowel. They offer a consistent way to describe pronunciation. [IPA](https://en.wikipedia.org/wiki/International_Phonetic_Alphabet) is the most widely used, especially in dictionaries, and has the advantage of being international.

In our case, since PoetWrite is focused on the English language, IPA is a bit overkill so we can use a much more basic system.

Going back to the CMU dictionary, let's take a look at how a word is represented according to their system.

```
CALCULATION --> K AE2 L K Y AH0 L EY1 SH AH0 N
```

Here, you can see the word _'calculation'_ being represented as the set of sounds that transcibes its pronunciation. The syntax used is known as [_ARPAbet_](https://en.wikipedia.org/wiki/ARPABET).

❓ APPAbet is being used as the base format internally in PoetWrite when doing the various analyses since it's straightforward to read, easy to parse and pretty trivial to convert from other phonetic transcription systems.

⛅ Do you see how the vowel phones have a number attached to them? These represent the stress on the vowel. In the future, this could be used for meter detection.

## Non-Dictionary Words

What happens if a word isn't in CMU? This is where the earlier mention of TTS systems comes into play. What if we could get the phonemes from the synthesis and output them as phonemes instead of audio? And take advantage of its powerful heuristics?

[_MaryTTS_](https://marytts.github.io/) is a light-weight Java TTS platform that has phoneme output. We'll be using the allophone output.

Again, for the same word _'calculation'_, this is the MaryTTS output.

```
<maryxml>
  <p>
    <s>
      <phrase>
        <t accent="!H*" g2p_method="lexicon" ph="' k { l - k j @ - l EI - S @ n" pos="NN">
          calculation
          <syllable accent="!H*" ph="k { l" stress="1"><ph p="k"/><ph p="{"/><ph p="l"/></syllable>
          <syllable ph="k j @"><ph p="k"/><ph p="j"/><ph p="@"/></syllable>
          <syllable ph="l EI"><ph p="l"/><ph p="EI"/></syllable>
          <syllable ph="S @ n"><ph p="S"/><ph p="@"/><ph p="n"/></syllable>
        </t>
        <boundary breakindex="5" tone="L-L%"/>
      </phrase>
    </s>
  </p>
</maryxml>

```

As you can see, it lists every syllable in the _ph_ attribute. We can combine them and get the following phoneme transcription.

```
k { l       k j @       l EI       S @ n 
```

Note that the phoneme representation is different than what CMU provides. This one is [_SAMPA_](https://en.wikipedia.org/wiki/SAMPA) which is very commonly used by various text-to-speech engines. It's similar to IPA but not as comprehensive.

Converting them to ARBApet is actually pretty simple. You can use a conversion table like this one: [English Arpabet to SAMPA mapping](https://ufal.mff.cuni.cz/~odusek/courses/npfl123/data/arpabet_to_sampa.html)

# Rhetorical Analysis

## Couting Syllables

❗**Syllables** are a single unit of pronunciation which corresponds to a certain vowel. The vowels can be surrounded with consonant.

For example, the word _'calculation'_ is split into 4 syllables
`cal-cu-la-tion`.

Knowing this, you can see that counting syllables basically involves counting the vowels within the phonetic translation.

```
CALCULATION --> K AE2 L K Y AH0 L EY1 SH AH0 N
                  ^^^       ^^^   ^^^    ^^^
                  |||       |||   |||    |||
                    4 vowels == 4 syllables
```

Therefore, the word _'calculation'_ has 4 syllables.

That's it!

## Detecting Rhymes

❗A **Rhyme** is when two words have the same ending in terms of sound.

✔️ For simplicity, two words rhyme if they have the same phonemes at the end of the word. 

⛅ PoetWrite is intended to have more complicated rhyming scheme detections such as slant and partial rhymes in the future.

Let's go for an interesting example, with the words _'calculation'_ and _'speculation'_. They rhyme because they both end with ```cu-la-tion```

This is what their ARPAbet transcription look like.

```
CALCULATION  K AE2 L K Y AH0 L EY1 SH AH0 N
SPECULATION  S P EH2 K Y AH0 L EY1 SH AH0 N
```

If you pay attention, you will see that there is a string of phonemes that match and align at the end. Those matching phonemes correspond to a rhyme.

```
CALCULATION  K AE2 L K Y AH0 L EY1 SH AH0 N
SPECULATION  S P EH2 K Y AH0 L EY1 SH AH0 N
                     ^ ^ ^^^ ^ ^^^ ^^ ^^^ ^                     
                     | | ||| | ||| || ||| |
                  Matching phonemes == Rhyming
```

If you count the number of vowels in the matching phoneme segment, that will respresent 4 syllables of rhyming.

⛅ Knowing how many syllables rhyme will become key for more advanced rhyming pattern detection.