package org.sharesanitizer.app

import org.junit.Assert.assertEquals
import org.junit.Test
import org.sharesanitizer.app.sanitizer.TextCleaner

class TextCleanerTest {

    @Test
    fun stripsZeroWidthSpaces() {
        val input = "hello\u200Bworld"
        val result = TextCleaner.clean(input)
        assertEquals("helloworld", result.cleaned)
        assertEquals(1, result.removedCount)
    }

    @Test
    fun stripsMultipleInvisibleCharTypes() {
        val input = "\uFEFFHello\u200B \u200Cworld\u200D!"
        val result = TextCleaner.clean(input)
        assertEquals("Hello world!", result.cleaned)
        assertEquals(4, result.removedCount)
    }

    @Test
    fun preservesNormalText() {
        val input = "This is perfectly normal text with spaces and punctuation!"
        val result = TextCleaner.clean(input)
        assertEquals(input, result.cleaned)
        assertEquals(0, result.removedCount)
    }

    @Test
    fun handlesEmptyString() {
        val result = TextCleaner.clean("")
        assertEquals("", result.cleaned)
        assertEquals(0, result.removedCount)
    }

    @Test
    fun stripsDirectionalMarks() {
        val input = "text\u200Ewith\u200Fmarks"
        val result = TextCleaner.clean(input)
        assertEquals("textwithmarks", result.cleaned)
        assertEquals(2, result.removedCount)
    }

    @Test
    fun preservesNewlinesAndTabs() {
        val input = "line1\nline2\ttab"
        val result = TextCleaner.clean(input)
        assertEquals(input, result.cleaned)
        assertEquals(0, result.removedCount)
    }

    @Test
    fun stripsSoftHyphen() {
        val input = "anti\u00ADvirus"
        val result = TextCleaner.clean(input)
        assertEquals("antivirus", result.cleaned)
        assertEquals(1, result.removedCount)
    }
}
