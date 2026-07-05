package org.sharesanitizer.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.sharesanitizer.app.sanitizer.RedirectUnwrapper

class RedirectUnwrapperTest {

    @Test
    fun unwrapsGoogleRedirect() {
        val input = "https://www.google.com/url?q=https%3A%2F%2Fexample.com%2Fpage&sa=D&ust=123"
        val result = RedirectUnwrapper.unwrap(input)
        assertEquals("https://example.com/page", result.url)
        assertTrue(result.wasUnwrapped)
    }

    @Test
    fun unwrapsFacebookRedirect() {
        val input = "https://l.facebook.com/l.php?u=https%3A%2F%2Fexample.com&h=abc123"
        val result = RedirectUnwrapper.unwrap(input)
        assertEquals("https://example.com", result.url)
        assertTrue(result.wasUnwrapped)
    }

    @Test
    fun unwrapsYouTubeRedirect() {
        val input = "https://www.youtube.com/redirect?q=https%3A%2F%2Fexample.org&event=video_description"
        val result = RedirectUnwrapper.unwrap(input)
        assertEquals("https://example.org", result.url)
        assertTrue(result.wasUnwrapped)
    }

    @Test
    fun unwrapsVkRedirect() {
        val input = "https://vk.com/away.php?to=https%3A%2F%2Fexample.net&uid=123"
        val result = RedirectUnwrapper.unwrap(input)
        assertEquals("https://example.net", result.url)
        assertTrue(result.wasUnwrapped)
    }

    @Test
    fun unwrapsSteamRedirect() {
        val input = "https://steamcommunity.com/linkfilter/?url=https%3A%2F%2Fexample.com"
        val result = RedirectUnwrapper.unwrap(input)
        assertEquals("https://example.com", result.url)
        assertTrue(result.wasUnwrapped)
    }

    @Test
    fun leavesNormalUrlAlone() {
        val input = "https://example.com/page?id=123"
        val result = RedirectUnwrapper.unwrap(input)
        assertEquals(input, result.url)
        assertFalse(result.wasUnwrapped)
    }

    @Test
    fun handlesInvalidUrl() {
        val input = "not-a-url"
        val result = RedirectUnwrapper.unwrap(input)
        assertEquals(input, result.url)
        assertFalse(result.wasUnwrapped)
    }

    @Test
    fun doesNotUnwrapNonHttpDestination() {
        val input = "https://www.google.com/url?q=javascript%3Aalert(1)&sa=D"
        val result = RedirectUnwrapper.unwrap(input)
        assertFalse(result.wasUnwrapped)
    }
}
