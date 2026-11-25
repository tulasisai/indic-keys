# indic-keys

[![Test](https://github.com/tulasisai/indic-keys/actions/workflows/test.yml/badge.svg)](https://github.com/tulasisai/indic-keys/actions/workflows/test.yml)

Type Indian languages with English letters. A small, dependency-free Kotlin library that turns
`namaste` into **नमस्ते**, **నమస్తె**, **நமஸ்தெ** or **নমস্তে**, and gives you each script's alphabet
for building keyboards, pickers and learning apps.

Works on Android and any JVM (Java 17+). No network, no dictionary files, about 250 lines.

```kotlin
import indickeys.IndicScript
import indickeys.Transliterate
import indickeys.Alphabet

Transliterate.toScript("kaise ho?", IndicScript.DEVANAGARI)  // कैसे हो?
Transliterate.toScript("namastE", IndicScript.TELUGU)        // నమస్తే
Transliterate.toScript("vaNakkam", IndicScript.TAMIL)        // வணக்கம்
Transliterate.toScript("nandi", IndicScript.MALAYALAM)       // നന്ദി

Alphabet.of(IndicScript.TELUGU).withVowels("క")             // [క, కా, కి, కీ, కు, ...]
```

## Scripts

Devanagari (Hindi, Marathi, Nepali), Bengali (Bengali, Assamese), Gurmukhi (Punjabi), Gujarati,
Odia, Tamil, Telugu, Kannada and Malayalam.

## How to type

Small letters are the common sounds; a few capitals stand for letters English doesn't have.

| Type | Gets | Type | Gets |
|---|---|---|---|
| `a aa i ii/ee u uu/oo` | अ आ इ ई उ ऊ | `e ai o au` | ए ऐ ओ औ |
| `E O` | long ఏ ఓ in Tamil, Telugu, Kannada, Malayalam (`e o` are short there) | `R` | ऋ |
| `k kh g gh ng` | क ख ग घ ङ | `ch chh j jh ny` | च छ ज झ ञ |
| `T Th D Dh N` | ट ठ ड ढ ण | `t th d dh n` | त थ द ध न |
| `p ph/f b bh m` | प फ ब भ म | `y r l v/w` | य र ल व |
| `sh Sh s h L` | श ष स ह ळ | `x` | क्ष |
| `M` | ं (anusvara) | `H` | ः (visarga) |

Anything that isn't a letter (spaces, digits, punctuation) is kept as it is.

## How it works

Unicode lays out every Indian script's block the same way: क is at `0x0915`, క at `0x0C15`, க at
`0x0B95`. So each word is spelled once in Devanagari and then moved to the chosen script by the
block's offset, with a short list of stand-ins for letters a script lacks (Tamil writes क, ख, ग
and घ all as க).

Each word gets one reading. Real keyboards also rank several readings against a word list ("kal"
can be कल or काल); that part is up to your app.

## Install

Copy the three files in `src/main/kotlin/indickeys` into your project, or build the jar:

```
./gradlew jar
```

## Contributing

Issues and pull requests are welcome: better spellings for a language, a missing letter, a new
script. Please add a test in `src/test` with your change.

## Made by

The team behind [Keysa](https://keysa.in), the AI keyboard for Indian languages.

## License

MIT
