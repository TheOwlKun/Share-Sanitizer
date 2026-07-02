package org.sharesanitizer.app

import org.sharesanitizer.app.sanitizer.UrlSanitizer
import org.sharesanitizer.app.sanitizer.CommunityUrlRules
import org.junit.Assert.assertEquals
import org.junit.Test

class UrlSanitizerTest {

    @Test
    fun testSanitizeUrl_removesUtmParams() {
        val input = "Check this out: https://example.com/page?utm_source=twitter&utm_medium=social&id=123"
        val result = UrlSanitizer.sanitizeText(input)
        
        assertEquals("Check this out: https://example.com/page?id=123", result.cleaned)
        assertEquals(1, result.removed.size)
        assertEquals(2, result.removed[0].params.size) // source, medium
    }

    @Test
    fun testSanitizeUrl_noParams_doesNotModify() {
        val input = "Here is a link https://example.com"
        val result = UrlSanitizer.sanitizeText(input)
        
        assertEquals("Here is a link https://example.com", result.cleaned)
        assertEquals(0, result.removed.size)
    }

    @Test
    fun testSanitizeUrl_multipleUrls() {
        val input = "First https://test.com?utm_campaign=sale and second https://other.com?gclid=abc"
        val result = UrlSanitizer.sanitizeText(input)
        
        assertEquals("First https://test.com and second https://other.com", result.cleaned)
        assertEquals(2, result.removed.size)
    }

    @Test
    fun testSanitizeUrl_preservesEncodedValues() {
        val input = "https://example.com/search?q=hello%20world&utm_source=newsletter&next=%2Fdeals%3Fid%3D42"
        val result = UrlSanitizer.sanitizeText(input)

        assertEquals("https://example.com/search?q=hello%20world&next=%2Fdeals%3Fid%3D42", result.cleaned)
        assertEquals(listOf("utm_source"), result.removed[0].params)
    }

    @Test
    fun testSanitizeUrl_normalizesDoubleQueryMarkerWhenRemovingTrackingParams() {
        val input = "https://example.com/search??q=test&utm_source=google"
        val result = UrlSanitizer.sanitizeText(input)

        assertEquals("https://example.com/search?q=test", result.cleaned)
        assertEquals(listOf("utm_source"), result.removed[0].params)
    }

    @Test
    fun testSanitizeUrl_removesMixedCaseAndEncodedParamKeys() {
        val input = "https://example.com/?UtM_Source=x&fb%63lid=y&id=123"
        val result = UrlSanitizer.sanitizeText(input)

        assertEquals("https://example.com/?id=123", result.cleaned)
        assertEquals(listOf("UtM_Source", "fbclid"), result.removed[0].params)
    }

    @Test
    fun testSanitizeUrl_preservesSchemeLessUrlStyle() {
        val input = "Visit example.com/path?utm_medium=email&id=123"
        val result = UrlSanitizer.sanitizeText(input)

        assertEquals("Visit example.com/path?id=123", result.cleaned)
    }

    @Test
    fun testSanitizeUrl_preservesLongTldSchemeLessUrl() {
        val input = "Visit example.museum/path?utm_medium=email&id=123"
        val result = UrlSanitizer.sanitizeText(input)

        assertEquals("Visit example.museum/path?id=123", result.cleaned)
    }

    @Test
    fun testSanitizeUrl_handlesSchemeLessUrlWithQueryOnly() {
        val input = "Visit example.com?utm_medium=email&id=123"
        val result = UrlSanitizer.sanitizeText(input)

        assertEquals("Visit example.com?id=123", result.cleaned)
    }

    @Test
    fun testSanitizeUrl_customParamsAreTrimmedAndCaseInsensitive() {
        val input = "https://example.com/?Keep=1&SessionId=secret"
        val result = UrlSanitizer.sanitizeText(input, additionalParams = setOf(" sessionid "))

        assertEquals("https://example.com/?Keep=1", result.cleaned)
        assertEquals(listOf("SessionId"), result.removed[0].params)
    }

    @Test
    fun testSanitizeUrl_preservesAmbiguousFunctionalParamsByDefault() {
        val input = "https://example.com/?source=inbox&url=https%3A%2F%2Fexample.org%2Farticle&utm_source=email"
        val result = UrlSanitizer.sanitizeText(input)

        assertEquals("https://example.com/?source=inbox&url=https%3A%2F%2Fexample.org%2Farticle", result.cleaned)
        assertEquals(listOf("utm_source"), result.removed[0].params)
    }

    @Test
    fun testSanitizeUrl_customParamsCanRemoveAmbiguousParams() {
        val input = "https://example.com/?source=inbox&id=123"
        val result = UrlSanitizer.sanitizeText(input, additionalParams = setOf("source"))

        assertEquals("https://example.com/?id=123", result.cleaned)
        assertEquals(listOf("source"), result.removed[0].params)
    }

    @Test
    fun testSanitizeUrl_removesTrackingParamsInsideFragmentQuery() {
        val input = "https://example.com/#/product/42?utm_source=email&id=123"
        val result = UrlSanitizer.sanitizeText(input)

        assertEquals("https://example.com/#/product/42?id=123", result.cleaned)
        assertEquals(listOf("utm_source"), result.removed[0].params)
    }

    @Test
    fun testSanitizeUrl_communityRulesRemoveDomainSpecificAmbiguousParams() {
        val rules = CommunityUrlRules.fromSimpleProvider(
            urlPattern = "^https?:\\/\\/(?:[a-z0-9-]+\\.)*?shop\\.example",
            rules = listOf("qid", "ref_?")
        )
        val input = "https://shop.example/item?qid=abc&ref_=share&id=123"
        val result = UrlSanitizer.sanitizeText(input, communityRules = rules)

        assertEquals("https://shop.example/item?id=123", result.cleaned)
        assertEquals(listOf("qid", "ref_"), result.removed[0].params)
    }

    @Test
    fun testSanitizeUrl_communityRulesRespectExceptions() {
        val rules = CommunityUrlRules.fromSimpleProvider(
            urlPattern = "^https?:\\/\\/(?:[a-z0-9-]+\\.)*?shop\\.example",
            rules = listOf("qid"),
            exceptions = listOf("^https?:\\/\\/shop\\.example\\/checkout")
        )
        val input = "https://shop.example/checkout?qid=abc&id=123"
        val result = UrlSanitizer.sanitizeText(input, communityRules = rules)

        assertEquals(input, result.cleaned)
        assertEquals(0, result.removed.size)
    }

    @Test
    fun testSanitizeUrl_communityRulesRemoveReferralMarketingParams() {
        val rules = CommunityUrlRules.fromSimpleProvider(
            urlPattern = "^https?:\\/\\/(?:[a-z0-9-]+\\.)*?shop\\.example",
            referralMarketing = listOf("tag")
        )
        val input = "https://shop.example/item?tag=affiliate-21&id=123"
        val result = UrlSanitizer.sanitizeText(input, communityRules = rules)

        assertEquals("https://shop.example/item?id=123", result.cleaned)
        assertEquals(listOf("tag"), result.removed[0].params)
    }

    @Test
    fun testSanitizeUrl_multipleUrlsPreservesTextBetweenCleanedLinks() {
        val rules = CommunityUrlRules.fromSimpleProviders(
            CommunityUrlRules.SimpleProvider(
                urlPattern = "^https?:\\/\\/(?:[a-z0-9-]+\\.)*?amazon\\.in",
                rules = listOf("ref_?", "tag")
            ),
            CommunityUrlRules.SimpleProvider(
                urlPattern = "^https?:\\/\\/(?:[a-z0-9-]+\\.)*?youtube\\.com",
                rules = listOf("si")
            )
        )
        val input = "Check these out: https://youtube.com/watch?v=abc123&si=xyz789 and also https://amazon.in/dp/B01234ABCD/ref=sr_1_1?tag=someaff-21"
        val result = UrlSanitizer.sanitizeText(input, communityRules = rules)

        assertEquals(
            "Check these out: https://youtube.com/watch?v=abc123 and also https://amazon.in/dp/B01234ABCD",
            result.cleaned
        )
    }
}
