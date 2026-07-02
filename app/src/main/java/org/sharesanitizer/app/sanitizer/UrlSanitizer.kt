package org.sharesanitizer.app.sanitizer

import java.net.URI
import java.net.URISyntaxException
import java.net.URLDecoder

object UrlSanitizer {
    val defaultTrackingParams = setOf(
        "__da_seqno", "__hsfp", "__hssc", "__hstc", "__io_lv", "__s", "_bdadid", "_bhlid",
        "_branch_match_id", "_branch_referrer", "_caid", "_clde", "_cldee", "_cvid", "_ga", "_gid",
        "_gl", "_hsenc", "_hsmi", "_io_session_id", "_ke", "_ly_c", "_ly_r", "_ope", "_openstat",
        "_rdt_arg", "_rdt_idx", "_rdt_sid", "_sgm_action", "_sgm_campaign", "_sgm_pinned",
        "_sgm_source", "_sgm_term", "_x_ads_account", "_x_ads_creative_id", "_x_ads_id",
        "_x_ads_set", "_x_bg_adid", "_x_from", "_x_ns_catalog_id", "_x_ns_crt_id", "_x_ns_gid",
        "_x_ns_placement", "_x_ns_product_id", "_x_ns_site_id", "_x_ns_source", "_zucks_suid",
        "acampid", "acrid", "actbtn", "action_object_map", "action_ref_map", "action_type_map",
        "ad_id", "adbnr", "adc_publisher", "adc_token", "adcampaignid", "adcopy", "adef1043",
        "adfrom", "adg_ctx", "adid", "adj_campaign", "adj_creative", "adj_label", "adj_t",
        "adjust_adgroup", "adjust_campaign", "adjust_creative", "adjust_referrer", "adjust_t",
        "adjust_tracker", "adjust_tracker_limit", "adme5101", "admitad_uid", "adobe_mc_ref",
        "adobe_mc_sdid", "ads_identity_ads_promo_identity_placement_uid",
        "ads_identity_ads_promo_identity_site_uid", "adsterra_clid", "adsterra_placement_id",
        "adtag", "advertiser", "af", "af_ad", "af_adset", "af_channel", "af_click_lookback",
        "af_force_deeplink", "af_sub1", "af_xp", "aff", "aff_id", "aff_sub", "affid", "affname",
        "agentcode", "aiad_clid", "aid", "analytics_context", "analytics_trace_id", "argument",
        "artlist_aid", "asgtbndr", "asid", "asubid", "at_campaign", "at_campaign_type",
        "at_creation", "at_emailtype", "at_link", "at_link_id", "at_link_origin", "at_link_type",
        "at_medium", "at_ptr_name", "at_recipient_id", "at_recipient_list", "at_send_date", "awc",
        "bance_xuid", "bannercode", "bemobdata", "beyond_uzcvid", "beyond_uzmcvid", "brand",
        "bsft_aaid", "bsft_clkid", "bsft_eid", "bsft_ek", "bsft_mid", "bsft_uid", "btag",
        "camp_no", "campaign", "campaign_id", "cg_referrer", "channable", "cid", "cjdata",
        "cjevent", "clckid", "click", "clickid", "clickorigin", "clid", "cm_cr", "cm_me", "cmoa",
        "cmoa_pg", "cmpid", "cnac", "creative", "crid", "cstrackid", "cuid", "cx_click",
        "cx_recsorder", "cx_recswidget", "dc_data", "dclid", "deeplink", "device", "dicbo", "did",
        "dmai", "dpg_campaign", "dpg_content", "dpg_medium", "dpg_source", "dpid", "dtm_user_id",
        "dtp", "ea_med", "ea_src", "ebisadid", "ebisother1", "ebisother2", "ebisother3",
        "ebisother4", "ebisother5", "eccid", "ectag", "ecuid", "elq", "elqaid", "elqak", "elqat",
        "elqtrackid", "email_source", "email_token", "emcs_t", "eml-mediaplan", "eml-name",
        "eml-publisher", "ems_dl", "entry_point", "entrypoint", "erid", "eurl",
        "external_click_id", "famad_xuid", "fb_action_ids", "fb_action_types", "fb_asc",
        "fb_comment_id", "fb_ref", "fb_source", "fbadid", "fbclid", "fd_bridge_id", "feature",
        "flowpath_page_no", "flowpath_page_value", "fm", "fm_topics_id", "form", "from", "ftag",
        "gad_campaignid", "gad_source", "gbraid", "gci", "gclid", "gclsrc", "gps_adid", "gspk",
        "gsxid", "gtmpos", "guccounter", "guce_referrer", "guce_referrer_sig", "hhtmfrom", "host",
        "hsa_acc", "hsa_ad", "hsa_cam", "hsa_grp", "hsa_kw", "hsa_la", "hsa_mt", "hsa_net",
        "hsa_ol", "hsa_src", "hsa_tgt", "hsa_ver", "hsctatracking", "iasid", "icid", "iclid",
        "ig_mid", "ig_rid", "igshid", "iid", "imp_id", "index_ref", "infnews", "int_campaign",
        "int_content", "int_medium", "int_source", "int_term", "ip_address", "ir_adid",
        "ir_campaignid", "ir_partnerid", "irclickid", "irgwc", "is_mobile", "is_retargeting",
        "ismcid", "itid", "itm_campaign", "itm_content", "itm_medium", "itm_source", "itm_term",
        "janet", "jd", "jmtyclid", "jsclck", "jumpreferrer", "kid", "kxconf", "ld", "ldtag_cl",
        "li_fat_id", "lid", "line_uid", "link_contentid", "link_ref", "link_source", "link_userid",
        "linkcode", "linkid", "loclid", "login-new", "login-source", "lp_fol", "lsadname2", "lsed",
        "lt_r", "maf", "mall_cid", "matomo", "mc_cid", "mc_eid", "mcid", "meta_redirectid",
        "mindbox-click-id", "mindbox-message-key", "mkt_tok", "ml_subscriber",
        "ml_subscriber_hash", "mpid", "mr_download_reason", "mr_game_version", "mr_loader",
        "mrk_rec", "ms_rnd", "msclkid", "mt_adset", "mt_campaign", "mt_click_id", "mt_creative",
        "mt_link_id", "mt_medium", "mt_network", "mt_sub1", "mt_sub2", "mt_sub3", "mt_sub4",
        "mt_sub5", "mtm_campaign", "mtm_cid", "mtm_content", "mtm_group", "mtm_keyword",
        "mtm_medium", "mtm_placement", "mtm_source", "nb_expid_meta", "nb_placement", "nbt",
        "new_session", "nid", "nx_source", "obem", "octid", "oly_anon_id", "oly_enc_id",
        "ometria_campaign", "ometria_profile_id", "openexternalbrowser", "operation", "oprtrack",
        "origin", "origin_referral_type", "original_referer", "partner_extra", "partner_slug",
        "path", "pathway", "pd_rd_r", "pd_rd_w", "pd_rd_wg", "personaclick_input_query",
        "personaclick_search_query", "pf_rd_i", "pf_rd_m", "pf_rd_p", "pf_rd_r", "pf_rd_s",
        "pf_rd_t", "pgrp", "pid", "piwik_campaign", "piwik_kwd", "pk_campaign", "pk_medium",
        "pk_source", "pk_vid", "ppid", "promo", "ps_partner_key", "ps_xid", "pscd", "psid",
        "psprogram", "pstool", "qid", "raneaid", "ranmid", "ransiteid", "rb_clickid", "rdr_refr",
        "rdt_cid", "recommended_by", "recommended_code", "recruited_by_id", "recruiter",
        "redirect", "redirect_source", "ref", "ref_", "ref_cd", "ref_code", "ref_content",
        "referrer", "referrersource", "reftag", "response", "rmai", "rrid", "rsta", "rtkcid",
        "rubricalias", "run_key", "rurl", "s-id", "s_cid", "scid", "send_time", "share_id",
        "share_to", "sharedid", "shortlink", "site", "sms_click", "sms_source", "sms_uph",
        "source", "source_caller", "sourceid", "spartner", "spot_im_redirect_source", "sprefix",
        "sr", "src", "srclt", "srcmarker2", "srsltid", "sscid", "subid1", "subid2", "subid3",
        "sv1", "sv_campaign_id", "svlink", "tag", "taid", "tbl", "tcsack", "tduid", "tgclid",
        "tid", "timezone", "tksid", "trial_status", "trk", "trkinfo", "ttclid", "tw_medium",
        "tw_profile_id", "tw_source", "twclid", "unicorn_click_id", "unp_tpcid", "unptid", "url",
        "user_agent", "user_email_address", "usqp", "utm_ad", "utm_adgroup", "utm_adset",
        "utm_affiliate", "utm_brand", "utm_campaign", "utm_campaign_name", "utm_campaignid",
        "utm_channel", "utm_cid", "utm_compaign", "utm_content", "utm_creative", "utm_email",
        "utm_emailid", "utm_emcid", "utm_emmid", "utm_id", "utm_id_", "utm_journey_id",
        "utm_keyword", "utm_lob", "utm_medium", "utm_name", "utm_newsletterid", "utm_place",
        "utm_prid", "utm_product", "utm_pubreferrer", "utm_reader", "utm_referrer", "utm_serial",
        "utm_servlet", "utm_session", "utm_siteid", "utm_social", "utm_social-type", "utm_source",
        "utm_source_code", "utm_source_platform", "utm_supplier", "utm_swu", "utm_tag", "utm_term",
        "utm_umguk", "utm_unptid", "utm_userid", "utm_viz_id", "utm_wave", "utml_campaign",
        "utmtoken", "uzcid", "vc_lpp", "vero_conv", "vero_id", "version", "visitid",
        "vs_campaign_id", "vsm_cid", "vsm_pid", "vsm_type", "wa_ref", "waad", "wbraid", "wickedid",
        "winflncrtag", "wpid", "wt_mc", "x-a-medium", "x-clickref", "x-source", "xadid", "xmktid",
        "xtor", "yclid", "yj_r", "ym_tracking_id", "ymid", "ysclid", "zdroj",
        "th", "psc"
    )

    private val ambiguousDefaultParams = setOf(
        "campaign", "cid", "creative", "device", "feature", "from", "host", "path", "pid", "qid",
        "redirect", "ref", "source", "src", "tag", "url", "version"
    )

    private val urlRegex = "(?i)\\b((?:https?://|www\\d{0,3}[.]|[a-z0-9.\\-]+[.][a-z]{2,63}(?=[/?#]))(?:[^\\s()<>]+|\\((?:[^\\s()<>]+|(?:\\([^\\s()<>]+\\)))*\\))+(?:\\((?:[^\\s()<>]+|(?:\\([^\\s()<>]+\\)))*\\)|[^\\s`!()\\[\\]{};:'\".,<>?«»“”‘’]))".toRegex()

    fun sanitizeText(
        text: String,
        additionalParams: Set<String> = emptySet(),
        trimWhitespace: Boolean = false,
        collapseLines: Boolean = false,
        communityRules: CommunityUrlRules? = null
    ): SanitizeResult {
        val customParams = additionalParams
            .map { it.lowercase().trim() }
            .filter { it.isNotEmpty() }
            .toSet()
        val allParamsToRemove = ((defaultTrackingParams - ambiguousDefaultParams) + customParams)
            .map { it.lowercase().trim() }
            .filter { it.isNotEmpty() }
            .toSet()
        val removedParams = mutableListOf<RemovedParam>()
        var cleanedText = text

        val matches = urlRegex.findAll(text).toList().reversed()

        for (match in matches) {
            val originalUrlString = match.value
            try {
                // Ensure URI can parse it (handle missing scheme)
                val uriStr = if (!originalUrlString.startsWith("http", ignoreCase = true)) "http://$originalUrlString" else originalUrlString
                val uri = URI(uriStr)
                val communityRulePatterns = communityRules?.rulesFor(uriStr).orEmpty()
                
                var isModified = false
                val paramsRemovedFromThisUrl = mutableListOf<String>()
                
                // 1. Process Path
                var newPath = uri.rawPath
                if (newPath != null) {
                    val segments = newPath.split("/")
                    val newSegments = mutableListOf<String>()
                    var pathModified = false
                    
                    for (segment in segments) {
                        val parts = segment.split("=", limit = 2)
                        val key = decodeParameterKey(parts[0])
                        
                        if (parts.size == 2 && shouldRemoveParameter(parts[0], key, allParamsToRemove, communityRulePatterns)) {
                            paramsRemovedFromThisUrl.add(key)
                            pathModified = true
                        } else if (segment.lowercase().startsWith("ref_") && allParamsToRemove.contains("ref_")) {
                            paramsRemovedFromThisUrl.add("ref_")
                            pathModified = true
                        } else {
                            newSegments.add(segment)
                        }
                    }
                    if (pathModified) {
                        newPath = newSegments.joinToString("/")
                        isModified = true
                    }
                }

                // 2. Process Query
                val queryResult = sanitizeParameterString(uri.rawQuery, allParamsToRemove, communityRulePatterns)
                val newQuery = queryResult.cleaned
                if (queryResult.removed.isNotEmpty()) {
                    paramsRemovedFromThisUrl.addAll(queryResult.removed)
                    isModified = true
                }

                val fragmentResult = sanitizeFragment(uri.rawFragment, allParamsToRemove, communityRulePatterns)
                val newFragment = fragmentResult.cleaned
                if (fragmentResult.removed.isNotEmpty()) {
                    paramsRemovedFromThisUrl.addAll(fragmentResult.removed)
                    isModified = true
                }
                
                if (isModified && paramsRemovedFromThisUrl.isNotEmpty()) {
                    val newUriStr = buildString {
                        // If original didn't have scheme, we added it for parsing, let's try to match original intent
                        val hasScheme = originalUrlString.startsWith("http", ignoreCase = true)
                        if (hasScheme) {
                            append(uri.scheme).append("://")
                        }
                        if (uri.rawAuthority != null) append(uri.rawAuthority)
                        if (newPath != null) append(newPath)
                        if (newQuery != null) append("?").append(newQuery)
                        if (newFragment != null) append("#").append(newFragment)
                    }
                    
                    // Replace in text
                    val start = match.range.first
                    val end = match.range.last + 1
                    cleanedText = cleanedText.substring(0, start) + newUriStr + cleanedText.substring(end)
                    
                    removedParams.add(RemovedParam(originalUrlString, paramsRemovedFromThisUrl))
                }
            } catch (e: URISyntaxException) {
                // Ignore malformed URL
            } catch (e: Exception) {
                // Catch other potential parsing issues
            }
        }
        
        if (trimWhitespace) {
            cleanedText = cleanedText.trim()
        }
        if (collapseLines) {
            cleanedText = cleanedText.replace(Regex("\\n{3,}"), "\n\n")
        }
        
        return SanitizeResult(text, cleanedText, removedParams.reversed())
    }

    private fun decodeParameterKey(key: String): String = try {
        URLDecoder.decode(key, "UTF-8")
    } catch (e: IllegalArgumentException) {
        key
    }

    private fun sanitizeParameterString(
        rawParams: String?,
        allParamsToRemove: Set<String>,
        communityRulePatterns: List<Regex>
    ): ParameterSanitizeResult {
        if (rawParams == null) return ParameterSanitizeResult(null, emptyList())

        val normalizedParams = rawParams.dropWhile { it == '?' }
        val params = normalizedParams.split("&")
        val keptParams = mutableListOf<String>()
        val removedParams = mutableListOf<String>()

        for (param in params) {
            val rawKey = param.split("=", limit = 2)[0]
            val key = decodeParameterKey(rawKey)
            val displayKey = key.removePrefix("amp;")
            if (shouldRemoveParameter(rawKey, displayKey, allParamsToRemove, communityRulePatterns)) {
                removedParams.add(displayKey)
            } else {
                keptParams.add(param)
            }
        }

        val cleaned = if (removedParams.isEmpty()) {
            normalizedParams
        } else {
            keptParams.filter { it.isNotEmpty() }.joinToString("&").ifEmpty { null }
        }

        return ParameterSanitizeResult(cleaned, removedParams)
    }

    private fun sanitizeFragment(
        rawFragment: String?,
        allParamsToRemove: Set<String>,
        communityRulePatterns: List<Regex>
    ): ParameterSanitizeResult {
        if (rawFragment == null) return ParameterSanitizeResult(null, emptyList())

        val questionMarkIndex = rawFragment.indexOf('?')
        if (questionMarkIndex >= 0) {
            val route = rawFragment.substring(0, questionMarkIndex)
            val params = rawFragment.substring(questionMarkIndex + 1)
            val result = sanitizeParameterString(params, allParamsToRemove, communityRulePatterns)
            val cleaned = if (result.removed.isEmpty()) {
                rawFragment
            } else if (result.cleaned == null) {
                route
            } else {
                "$route?${result.cleaned}"
            }
            return ParameterSanitizeResult(cleaned, result.removed)
        }

        if (!rawFragment.contains("=")) {
            return ParameterSanitizeResult(rawFragment, emptyList())
        }

        return sanitizeParameterString(rawFragment, allParamsToRemove, communityRulePatterns)
    }

    private fun shouldRemoveParameter(
        rawKey: String,
        key: String,
        allParamsToRemove: Set<String>,
        communityRulePatterns: List<Regex>
    ): Boolean {
        val normalizedKey = key.removePrefix("amp;").lowercase()
        return allParamsToRemove.contains(normalizedKey) ||
            communityRulePatterns.any { pattern ->
                pattern.matches(rawKey) || pattern.matches(key) || pattern.matches(normalizedKey)
            }
    }
}

private data class ParameterSanitizeResult(
    val cleaned: String?,
    val removed: List<String>
)

data class RemovedParam(
    val url: String,
    val params: List<String>
)

data class SanitizeResult(
    val original: String,
    val cleaned: String,
    val removed: List<RemovedParam>
)

