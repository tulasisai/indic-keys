package indickeys

/**
 * A script's letters in school-chart order: handy for building a keyboard, a picker or a learning
 * app. [vowelSigns] pairs each vowel with the sign it becomes after a consonant (आ -> ा).
 */
data class Alphabet(
    val vowels: List<String>,
    val vowelSigns: Map<String, String>,
    val consonants: List<String>,
    val digits: List<String>,
) {
    /** [consonant] with every vowel sign: क का कि की कु... (the "barakhadi" chart). */
    fun withVowels(consonant: String): List<String> =
        vowels.map { v -> consonant + vowelSigns.getOrDefault(v, "") }

    companion object {
        private val VOWELS = listOf("अ", "आ", "इ", "ई", "उ", "ऊ", "ऋ", "ए", "ऐ", "ओ", "औ")
        private val DRAVIDIAN_VOWELS = listOf("अ", "आ", "इ", "ई", "उ", "ऊ", "ऋ", "ऎ", "ए", "ऐ", "ऒ", "ओ", "औ")
        private val SIGNS = mapOf(
            "आ" to "ा", "इ" to "ि", "ई" to "ी", "उ" to "ु", "ऊ" to "ू", "ऋ" to "ृ",
            "ऎ" to "ॆ", "ए" to "े", "ऐ" to "ै", "ऒ" to "ॊ", "ओ" to "ो", "औ" to "ौ",
        )
        private val CONSONANTS = listOf(
            "क", "ख", "ग", "घ", "ङ", "च", "छ", "ज", "झ", "ञ", "ट", "ठ", "ड", "ढ", "ण",
            "त", "थ", "द", "ध", "न", "प", "फ", "ब", "भ", "म", "य", "र", "ल", "व", "श", "ष", "स", "ह",
        )
        private val DIGITS = listOf("०", "१", "२", "३", "४", "५", "६", "७", "८", "९")

        /** The alphabet of [script]. Letters the script doesn't have are left out. */
        fun of(script: IndicScript): Alphabet {
            val vowels = if (script.dravidian) DRAVIDIAN_VOWELS else VOWELS
            fun write(list: List<String>) = list.map(script::fromDevanagari).filter { it.isNative(script) }.distinct()
            return Alphabet(
                vowels = write(vowels),
                vowelSigns = vowels.filter { it in SIGNS }.associate { script.fromDevanagari(it) to script.fromDevanagari(SIGNS.getValue(it)) }
                    .filterKeys { it.isNative(script) },
                consonants = write(CONSONANTS),
                digits = DIGITS.map(script::fromDevanagari),
            )
        }

        // Still in Devanagari means the script has no such letter.
        private fun String.isNative(script: IndicScript) =
            script == IndicScript.DEVANAGARI || none { it.code in 0x0900..0x097F }
    }
}
