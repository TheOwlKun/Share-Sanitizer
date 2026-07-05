package org.sharesanitizer.app.sanitizer

object TextCleaner {

    private val invisibleChars = setOf(
        '\u200B', // ZERO WIDTH SPACE
        '\u200C', // ZERO WIDTH NON-JOINER
        '\u200D', // ZERO WIDTH JOINER
        '\u200E', // LEFT-TO-RIGHT MARK
        '\u200F', // RIGHT-TO-LEFT MARK
        '\u2060', // WORD JOINER
        '\u2061', // FUNCTION APPLICATION
        '\u2062', // INVISIBLE TIMES
        '\u2063', // INVISIBLE SEPARATOR
        '\u2064', // INVISIBLE PLUS
        '\uFEFF', // ZERO WIDTH NO-BREAK SPACE (BOM)
        '\u00AD', // SOFT HYPHEN
        '\u034F', // COMBINING GRAPHEME JOINER
        '\u061C', // ARABIC LETTER MARK
        '\u180E', // MONGOLIAN VOWEL SEPARATOR
        '\u2028', // LINE SEPARATOR (invisible)
        '\u2029', // PARAGRAPH SEPARATOR (invisible)
        '\u202A', // LEFT-TO-RIGHT EMBEDDING
        '\u202B', // RIGHT-TO-LEFT EMBEDDING
        '\u202C', // POP DIRECTIONAL FORMATTING
        '\u202D', // LEFT-TO-RIGHT OVERRIDE
        '\u202E', // RIGHT-TO-LEFT OVERRIDE
        '\u2066', // LEFT-TO-RIGHT ISOLATE
        '\u2067', // RIGHT-TO-LEFT ISOLATE
        '\u2068', // FIRST STRONG ISOLATE
        '\u2069', // POP DIRECTIONAL ISOLATE
    )

    fun clean(text: String): TextCleanResult {
        var removed = 0
        val cleaned = buildString(text.length) {
            for (ch in text) {
                if (ch in invisibleChars) {
                    removed++
                } else {
                    append(ch)
                }
            }
        }
        return TextCleanResult(cleaned, removed)
    }
}

data class TextCleanResult(
    val cleaned: String,
    val removedCount: Int
)
