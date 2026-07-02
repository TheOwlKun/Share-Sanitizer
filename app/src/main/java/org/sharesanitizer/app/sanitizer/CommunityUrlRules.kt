package org.sharesanitizer.app.sanitizer

import android.content.Context
import org.json.JSONObject

class CommunityUrlRules private constructor(
    private val providers: List<Provider>
) {
    fun rulesFor(url: String): List<Regex> {
        val matchedRules = mutableListOf<Regex>()

        for (provider in providers) {
            if (!provider.urlPattern.containsMatchIn(url)) continue
            if (provider.exceptions.any { it.containsMatchIn(url) }) continue

            matchedRules.addAll(provider.rules)
        }

        return matchedRules
    }

    companion object {
        private const val ASSET_NAME = "community_url_rules.minify.json"

        @Volatile
        private var cachedRules: CommunityUrlRules? = null

        fun load(context: Context): CommunityUrlRules? {
            cachedRules?.let { return it }

            return try {
                val json = context.assets.open(ASSET_NAME).bufferedReader().use { it.readText() }
                parse(json).also { cachedRules = it }
            } catch (e: Exception) {
                null
            }
        }

        internal fun parse(json: String): CommunityUrlRules {
            val providersJson = JSONObject(json).getJSONObject("providers")
            val providers = mutableListOf<Provider>()
            val providerNames = providersJson.keys()

            while (providerNames.hasNext()) {
                val providerName = providerNames.next()
                val providerJson = providersJson.getJSONObject(providerName)
                val urlPattern = providerJson.optString("urlPattern", "")
                if (urlPattern.isBlank()) continue
                val compiledUrlPattern = urlPattern.toSafeRegex() ?: continue

                providers.add(
                    Provider(
                        urlPattern = compiledUrlPattern,
                        rules = (providerJson.optStringArray("rules") + providerJson.optStringArray("referralMarketing"))
                            .mapNotNull { it.toSafeRegex() },
                        exceptions = providerJson.optStringArray("exceptions").mapNotNull { it.toSafeRegex() }
                    )
                )
            }

            // Inject custom patches for known tracking gaps in ClearURLs
            providers.add(
                Provider(
                    urlPattern = Regex("^https?:\\/\\/(?:[a-z0-9-]+\\.)*?(?:instagram\\.com|cdninstagram\\.com)", RegexOption.IGNORE_CASE),
                    rules = listOf(Regex("_nc_ht", RegexOption.IGNORE_CASE), Regex("_nc_cat", RegexOption.IGNORE_CASE), Regex("_nc_ohc", RegexOption.IGNORE_CASE), Regex("edm", RegexOption.IGNORE_CASE), Regex("ccb", RegexOption.IGNORE_CASE), Regex("ig_cache_key", RegexOption.IGNORE_CASE), Regex("oh", RegexOption.IGNORE_CASE), Regex("oe", RegexOption.IGNORE_CASE), Regex("igsh", RegexOption.IGNORE_CASE)),
                    exceptions = emptyList()
                )
            )
            providers.add(
                Provider(
                    urlPattern = Regex("^https?:\\/\\/(?:[a-z0-9-]+\\.)*?amazon\\.(?:com|co\\.jp|co\\.uk|de|es|fr|in|it|ca|com\\.au|com\\.br|com\\.mx)", RegexOption.IGNORE_CASE),
                    rules = listOf(Regex("tag", RegexOption.IGNORE_CASE), Regex("linkCode", RegexOption.IGNORE_CASE), Regex("linkId", RegexOption.IGNORE_CASE), Regex("crid", RegexOption.IGNORE_CASE), Regex("qid", RegexOption.IGNORE_CASE), Regex("sprefix", RegexOption.IGNORE_CASE), Regex("sr", RegexOption.IGNORE_CASE), Regex("ref_", RegexOption.IGNORE_CASE), Regex("psc", RegexOption.IGNORE_CASE), Regex("th", RegexOption.IGNORE_CASE)),
                    exceptions = emptyList()
                )
            )

            return CommunityUrlRules(providers)
        }

        internal fun fromSimpleProvider(
            urlPattern: String,
            rules: List<String> = emptyList(),
            referralMarketing: List<String> = emptyList(),
            exceptions: List<String> = emptyList()
        ): CommunityUrlRules {
            return CommunityUrlRules(
                listOf(
                    Provider(
                        urlPattern = urlPattern.toSafeRegex() ?: Regex("a^"),
                        rules = (rules + referralMarketing).mapNotNull { it.toSafeRegex() },
                        exceptions = exceptions.mapNotNull { it.toSafeRegex() }
                    )
                )
            )
        }

        internal fun fromSimpleProviders(
            vararg providers: SimpleProvider
        ): CommunityUrlRules {
            return CommunityUrlRules(
                providers.map {
                    Provider(
                        urlPattern = it.urlPattern.toSafeRegex() ?: Regex("a^"),
                        rules = (it.rules + it.referralMarketing).mapNotNull { rule -> rule.toSafeRegex() },
                        exceptions = it.exceptions.mapNotNull { exception -> exception.toSafeRegex() }
                    )
                }
            )
        }

        private fun JSONObject.optStringArray(name: String): List<String> {
            val array = optJSONArray(name) ?: return emptyList()
            return List(array.length()) { index -> array.optString(index) }
                .filter { it.isNotBlank() }
        }

        private fun String.toSafeRegex(): Regex? = try {
            Regex(this, RegexOption.IGNORE_CASE)
        } catch (e: Exception) {
            null
        }
    }

    private data class Provider(
        val urlPattern: Regex,
        val rules: List<Regex>,
        val exceptions: List<Regex>
    )

    internal data class SimpleProvider(
        val urlPattern: String,
        val rules: List<String> = emptyList(),
        val referralMarketing: List<String> = emptyList(),
        val exceptions: List<String> = emptyList()
    )
}
