package indickeys

/**
 * The nine major Indian scripts.
 *
 * Unicode gives every Indian script a 128-character block laid out the same way, so a letter sits at
 * the same place in each block (क at 0x0915, క at 0x0C15, க at 0x0B95...). That lets text written once
 * in Devanagari be moved to any of them by adding the block's offset. Letters a script doesn't have
 * are swapped for the nearest one it does ([fallbacks]).
 *
 * [dravidian] scripts (Tamil, Telugu, Kannada, Malayalam) have short and long e and o, and end a
 * word that finishes in a consonant with a virama (బస్), not a silent "a".
 */
enum class IndicScript(
    val displayName: String,
    private val blockStart: Int,
    val dravidian: Boolean,
    private val fallbacks: Map<Char, String> = emptyMap(),
) {
    DEVANAGARI("Devanagari (Hindi, Marathi, Nepali)", 0x0900, dravidian = false),
    BENGALI(
        "Bengali (Bengali, Assamese)", 0x0980, dravidian = false,
        fallbacks = mapOf('व' to "ব", 'ळ' to "ল", 'ऎ' to "এ", 'ॆ' to "ে", 'ऒ' to "ও", 'ॊ' to "ো"),
    ),
    GURMUKHI(
        "Gurmukhi (Punjabi)", 0x0A00, dravidian = false,
        fallbacks = mapOf('ऋ' to "ਰਿ", 'ृ' to "੍ਰ", 'ष' to "ਸ਼", 'ऎ' to "ਏ", 'ॆ' to "ੇ", 'ऒ' to "ਓ", 'ॊ' to "ੋ"),
    ),
    GUJARATI(
        "Gujarati", 0x0A80, dravidian = false,
        fallbacks = mapOf('ऎ' to "એ", 'ॆ' to "ે", 'ऒ' to "ઓ", 'ॊ' to "ો"),
    ),
    ODIA(
        "Odia", 0x0B00, dravidian = false,
        fallbacks = mapOf('ऎ' to "ଏ", 'ॆ' to "େ", 'ऒ' to "ଓ", 'ॊ' to "ୋ"),
    ),
    TAMIL(
        "Tamil", 0x0B80, dravidian = true,
        // Tamil writes one letter for each group: k, kh, g and gh are all க.
        fallbacks = mapOf(
            'ख' to "க", 'ग' to "க", 'घ' to "க", 'छ' to "ச", 'झ' to "ஜ", 'ठ' to "ட", 'ड' to "ட", 'ढ' to "ட",
            'थ' to "த", 'द' to "த", 'ध' to "த", 'फ' to "ப", 'ब' to "ப", 'भ' to "ப", 'श' to "ஶ",
            'ऋ' to "ரு", 'ृ' to "ு", 'ं' to "ம்",
        ),
    ),
    TELUGU("Telugu", 0x0C00, dravidian = true),
    KANNADA("Kannada", 0x0C80, dravidian = true),
    MALAYALAM("Malayalam", 0x0D00, dravidian = true);

    /** [devanagari] text written in this script. Characters outside Devanagari are kept as they are. */
    fun fromDevanagari(devanagari: String): String = buildString {
        for (c in devanagari) {
            if (c.code !in 0x0900..0x097F) {
                append(c)
                continue
            }
            val fallback = fallbacks[c]
            val moved = blockStart + (c.code - 0x0900)
            when {
                fallback != null -> append(fallback)
                Character.isDefined(moved) -> append(moved.toChar())
                else -> append(c) // no such letter, and no stand-in: keep the Devanagari one
            }
        }
    }
}
