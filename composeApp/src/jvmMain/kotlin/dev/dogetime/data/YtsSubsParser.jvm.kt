package dev.dogetime.data

import dev.dogetime.domain.Subtitle
import org.jsoup.Jsoup
import java.util.Base64

internal actual fun parseYtsSubsMoviePage(html: String, baseUrl: String): List<Subtitle> {
    val doc = Jsoup.parse(html, baseUrl)
    val tbody = doc.selectFirst("tbody") ?: return emptyList()
    val rows = tbody.select("tr")

    return rows.mapNotNull { row ->
        val lang = row.selectFirst(".sub-lang")?.text()?.trim().orEmpty()
        if (lang.isBlank()) return@mapNotNull null

        val linkEl = row.selectFirst("a[href*=/subtitles/]") ?: return@mapNotNull null
        val relHref = linkEl.attr("href").trim()
        if (relHref.isBlank()) return@mapNotNull null

        val pageUrl = if (relHref.startsWith("http")) relHref else "$baseUrl$relHref"
        val releaseName = linkEl.text().removePrefix("subtitle").trim().ifBlank {
            row.selectFirst("td:nth-child(3)")?.text()?.removePrefix("subtitle")?.trim().orEmpty()
        }
        val uploader = row.selectFirst(".uploader-cell")?.text()?.trim().orEmpty()

        Subtitle(
            releaseName = releaseName.ifBlank { "YTS Subtitle" },
            language = lang,
            author = uploader,
            downloadUrl = pageUrl,
            hearingImpaired = false,
            season = null,
            episode = null,
        )
    }
}

internal actual fun parseYtsSubsDownloadUrl(html: String): String? {
    val doc = Jsoup.parse(html)
    val downloadBtn = doc.selectFirst("#btn-download-subtitle, .btn-download-subtitle, a.download-subtitle, a[data-link]")
        ?: return null

    val encodedLink = downloadBtn.attr("data-link").trim()
    if (encodedLink.isBlank()) return null

    return try {
        val decodedBytes = Base64.getDecoder().decode(encodedLink)
        String(decodedBytes, Charsets.UTF_8)
    } catch (_: Exception) {
        null
    }
}
