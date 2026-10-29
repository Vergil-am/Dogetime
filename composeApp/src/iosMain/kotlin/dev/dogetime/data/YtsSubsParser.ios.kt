package dev.dogetime.data

import dev.dogetime.domain.Subtitle

internal actual fun parseYtsSubsMoviePage(html: String, baseUrl: String): List<Subtitle> = emptyList()

internal actual fun parseYtsSubsDownloadUrl(html: String): String? = null
