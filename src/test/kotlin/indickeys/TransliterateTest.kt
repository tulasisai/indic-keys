package indickeys

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TransliterateTest {
    private fun t(text: String, script: IndicScript) = Transliterate.toScript(text, script)

    @Test
    fun hindi() {
        assertEquals("नमस्ते", t("namaste", IndicScript.DEVANAGARI))
        assertEquals("कैसे हो?", t("kaise ho?", IndicScript.DEVANAGARI))
        assertEquals("भारत", t("bhaarat", IndicScript.DEVANAGARI))
        assertEquals("क्षमा", t("xamaa", IndicScript.DEVANAGARI))
    }

    @Test
    fun dravidian() {
        assertEquals("నమస్తే", t("namastE", IndicScript.TELUGU))
        assertEquals("బస్", t("bas", IndicScript.TELUGU))
        assertEquals("ವಣಕ್ಕ", t("vaNakka", IndicScript.KANNADA))
        assertEquals("வணக்கம்", t("vaNakkam", IndicScript.TAMIL))
        assertEquals("നന്ദി", t("nandi", IndicScript.MALAYALAM))
    }

    @Test
    fun otherScripts() {
        assertEquals("নমস্তে", t("namaste", IndicScript.BENGALI))
        assertEquals("ਨਮਸਤੇ", t("namasate", IndicScript.GURMUKHI))
        assertEquals("નમસ્તે", t("namaste", IndicScript.GUJARATI))
        assertEquals("ନମସ୍ତେ", t("namaste", IndicScript.ODIA))
    }

    @Test
    fun capitalsAreTheirOwnLetters() {
        assertEquals("टमाटर", t("TamaaTar", IndicScript.DEVANAGARI))
        assertEquals("बस", t("Bas", IndicScript.DEVANAGARI)) // B means nothing special: b
    }

    @Test
    fun keepsWhatIsNotLetters() {
        assertEquals("12, 34!", t("12, 34!", IndicScript.DEVANAGARI))
        assertEquals("हम 2 हैं.", t("ham 2 haiM.", IndicScript.DEVANAGARI))
        assertEquals("", t("", IndicScript.TELUGU))
    }

    @Test
    fun alphabet() {
        val telugu = Alphabet.of(IndicScript.TELUGU)
        assertEquals("అ", telugu.vowels.first())
        assertEquals(listOf("క", "కా", "కి"), telugu.withVowels("క").take(3))
        assertEquals("౦", telugu.digits.first())
        val tamil = Alphabet.of(IndicScript.TAMIL)
        assertTrue("க" in tamil.consonants)
        assertTrue(tamil.consonants.none { it.any { c -> c.code in 0x0900..0x097F } })
    }
}
