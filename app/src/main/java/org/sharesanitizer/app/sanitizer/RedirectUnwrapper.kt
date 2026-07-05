package org.sharesanitizer.app.sanitizer

import java.net.URI
import java.net.URLDecoder

object RedirectUnwrapper {

    private data class RedirectPattern(
        val hostSuffix: String,
        val pathPrefix: String,
        val paramName: String
    )

    private val patterns = listOf(
        RedirectPattern("google.com", "/url", "q"),
        RedirectPattern("google.com", "/url", "url"),
        RedirectPattern("facebook.com", "/l.php", "u"),
        RedirectPattern("l.facebook.com", "/l.php", "u"),
        RedirectPattern("lm.facebook.com", "/l.php", "u"),
        RedirectPattern("youtube.com", "/redirect", "q"),
        RedirectPattern("youtube.com", "/redirect", "redir_token"),
        RedirectPattern("linkedin.com", "/redir/redirect", "url"),
        RedirectPattern("linkedin.com", "/slink", "url"),
        RedirectPattern("steamcommunity.com", "/linkfilter/", "url"),
        RedirectPattern("steamcommunity.com", "/linkfilter/", "u"),
        RedirectPattern("vk.com", "/away.php", "to"),
        RedirectPattern("reddit.com", "/r/", "url"),
        RedirectPattern("curseforge.com", "/linkout", "remoteUrl"),
        RedirectPattern("bing.com", "/ck/a", "u"),
        RedirectPattern("deviantart.com", "/users/outgoing", ""),
        RedirectPattern("exit.sc", "", "url"),
    )

    private val genericParamNames = listOf("url", "uri", "target", "to", "link", "redirect", "out", "dest")

    fun unwrap(url: String): UnwrapResult {
        try {
            val uriStr = if (!url.startsWith("http", ignoreCase = true)) "http://$url" else url
            val uri = URI(uriStr)
            val host = uri.host?.lowercase() ?: return UnwrapResult(url, false)
            val path = uri.rawPath ?: ""

            for (pattern in patterns) {
                if (!host.endsWith(pattern.hostSuffix)) continue
                if (pattern.pathPrefix.isNotEmpty() && !path.startsWith(pattern.pathPrefix, ignoreCase = true)) continue

                if (pattern.paramName.isEmpty() && pattern.hostSuffix == "deviantart.com") {
                    val idx = path.indexOf("/users/outgoing?")
                    if (idx >= 0) {
                        val remainder = path.substring(idx + "/users/outgoing?".length)
                        val decoded = safeUrlDecode(remainder)
                        if (decoded.startsWith("http")) return UnwrapResult(decoded, true)
                    }
                    continue
                }

                val extracted = extractParam(uri.rawQuery, pattern.paramName)
                if (extracted != null) {
                    val decoded = safeUrlDecode(extracted)
                    if (decoded.startsWith("http", ignoreCase = true)) {
                        return UnwrapResult(decoded, true)
                    }
                }
            }

            // Generic fallback: look for common query parameters that might contain a URL
            val rawQuery = uri.rawQuery
            if (rawQuery != null) {
                for (paramName in genericParamNames) {
                    val extracted = extractParam(rawQuery, paramName)
                    if (extracted != null) {
                        val decoded = safeUrlDecode(extracted)
                        if (decoded.startsWith("http://", ignoreCase = true) || decoded.startsWith("https://", ignoreCase = true)) {
                            return UnwrapResult(decoded, true)
                        }
                    }
                }
            }
        } catch (_: Exception) { }

        return UnwrapResult(url, false)
    }

    private fun extractParam(rawQuery: String?, paramName: String): String? {
        if (rawQuery == null) return null
        val params = rawQuery.split("&")
        for (param in params) {
            val parts = param.split("=", limit = 2)
            if (parts.size == 2 && parts[0].equals(paramName, ignoreCase = true)) {
                return parts[1]
            }
        }
        return null
    }

    private fun safeUrlDecode(value: String): String = try {
        URLDecoder.decode(value, "UTF-8")
    } catch (_: Exception) {
        value
    }
}

data class UnwrapResult(
    val url: String,
    val wasUnwrapped: Boolean
)
