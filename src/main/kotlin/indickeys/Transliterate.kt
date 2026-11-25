package indickeys

import java.text.Normalizer

/**
 * Turns romanised text ("namaste", "bagunnara") into an Indian script.
 *
 * It's a simple phonetic scheme, close to how people text in English letters, with a few capitals
 * for letters English doesn't have:
 *
 * | Type | For | Type | For |
 * |---|---|---|---|
 * | a aa i ii/ee u uu/oo | अ आ इ ई उ ऊ | e ai o au | ए ऐ ओ औ |
 * | E O (Dravidian) | long ే ో (e/o are short) | R | ऋ |
 * | k kh g gh ng | क ख ग घ ङ | ch chh j jh ny | च छ ज झ ञ |
 * | T Th D Dh N | ट ठ ड ढ ण | t th d dh n | त थ द ध न |
 * | p ph/f b bh m | प फ ब भ म | y r l v/w | य र ल व |
 * | sh Sh s h L | श ष स ह ळ | x | क्ष |
 * | M | ं (anusvara) | H | ः (visarga) |
 *
 * Words are written one reading each (no dictionary), so "kal" is कल, never काल. Anything that isn't
 * a letter (spaces, digits, punctuation) is kept.
 */
object Transliterate {

    /** [text] in English letters, written in [script]. */
    fun toScript(text: String, script: IndicScript): String {
        val devanagari = WORD.replace(text) { toDevanagari(it.value, script.dravidian) }
        return Normalizer.normalize(script.fromDevanagari(devanagari), Normalizer.Form.NFC)
    }

    private val WORD = Regex("[A-Za-z]+")

    private const val VIRAMA = '्'

    // Each vowel on its own, and as the sign after a consonant (empty: the consonant's own "a").
    private class Vowel(val letter: String, val sign: String)

    private val VOWELS_NORTH = mapOf(
        "a" to Vowel("अ", ""), "aa" to Vowel("आ", "ा"), "A" to Vowel("आ", "ा"),
        "i" to Vowel("इ", "ि"), "ii" to Vowel("ई", "ी"), "ee" to Vowel("ई", "ी"), "I" to Vowel("ई", "ी"),
        "u" to Vowel("उ", "ु"), "uu" to Vowel("ऊ", "ू"), "oo" to Vowel("ऊ", "ू"), "U" to Vowel("ऊ", "ू"),
        "e" to Vowel("ए", "े"), "E" to Vowel("ए", "े"), "ai" to Vowel("ऐ", "ै"),
        "o" to Vowel("ओ", "ो"), "O" to Vowel("ओ", "ो"), "au" to Vowel("औ", "ौ"), "R" to Vowel("ऋ", "ृ"),
    )

    // Dravidian scripts: e and o are short, E and O long.
    private val VOWELS_SOUTH = VOWELS_NORTH + mapOf(
        "e" to Vowel("ऎ", "ॆ"), "o" to Vowel("ऒ", "ॊ"),
    )

    private val CONSONANTS = mapOf(
        "k" to "क", "kh" to "ख", "g" to "ग", "gh" to "घ", "ng" to "ङ",
        "ch" to "च", "chh" to "छ", "j" to "ज", "jh" to "झ", "ny" to "ञ",
        "T" to "ट", "Th" to "ठ", "D" to "ड", "Dh" to "ढ", "N" to "ण",
        "t" to "त", "th" to "थ", "d" to "द", "dh" to "ध", "n" to "न",
        "p" to "प", "ph" to "फ", "f" to "फ", "b" to "ब", "bh" to "भ", "m" to "म",
        "y" to "य", "r" to "र", "l" to "ल", "v" to "व", "w" to "व",
        "sh" to "श", "Sh" to "ष", "s" to "स", "h" to "ह", "L" to "ळ", "x" to "क्ष",
        "q" to "क", "c" to "क", "z" to "ज",
    )

    private val MARKS = mapOf("M" to "ं", "H" to "ः")

    private fun toDevanagari(word: String, dravidian: Boolean): String {
        val vowels = if (dravidian) VOWELS_SOUTH else VOWELS_NORTH
        val out = StringBuilder()
        var afterConsonant = false
        var i = 0
        while (i < word.length) {
            val match = longestMatch(word, i, vowels)
            if (match == null) { // a capital with no meaning of its own, kept as typed
                out.append(word[i])
                afterConsonant = false
                i++
                continue
            }
            val (key, kind, length) = match
            when (kind) {
                Kind.CONSONANT -> {
                    if (afterConsonant) out.append(VIRAMA)
                    out.append(CONSONANTS.getValue(key))
                    afterConsonant = true
                }
                Kind.VOWEL -> {
                    val v = vowels.getValue(key)
                    out.append(if (afterConsonant) v.sign else v.letter)
                    afterConsonant = false
                }
                Kind.MARK -> {
                    out.append(MARKS.getValue(key))
                    afterConsonant = false
                }
            }
            i += length
        }
        // "bas" in Telugu is బస్: a Dravidian word that ends in a consonant gets a virama.
        if (afterConsonant && dravidian) out.append(VIRAMA)
        return out.toString()
    }

    private enum class Kind { CONSONANT, VOWEL, MARK }

    private data class Match(val key: String, val kind: Kind, val length: Int)

    // Longest group first, so "kh" wins over "k" then "h". Capitals are letters of their own (T is
    // ट, t is त); a capital that means nothing ("B") is read as its small letter.
    private fun longestMatch(word: String, at: Int, vowels: Map<String, Vowel>): Match? {
        for (len in 3 downTo 1) {
            if (at + len > word.length) continue
            find(word.substring(at, at + len), vowels)?.let { return Match(it.first, it.second, len) }
        }
        return find(word[at].lowercase(), vowels)?.let { Match(it.first, it.second, 1) }
    }

    private fun find(piece: String, vowels: Map<String, Vowel>): Pair<String, Kind>? = when (piece) {
        in CONSONANTS -> piece to Kind.CONSONANT
        in vowels -> piece to Kind.VOWEL
        in MARKS -> piece to Kind.MARK
        else -> null
    }
}
