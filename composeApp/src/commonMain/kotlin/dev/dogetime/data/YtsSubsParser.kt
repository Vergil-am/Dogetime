package dev.dogetime.data

import dev.dogetime.domain.Subtitle

internal expect fun parseYtsSubsMoviePage(html: String, baseUrl: String): List<Subtitle>

internal expect fun parseYtsSubsDownloadUrl(html: String): String?
