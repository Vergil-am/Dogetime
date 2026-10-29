package dev.dogetime.domain

/** A single subtitle file available for download. */
data class Subtitle(
    /** Release or file name describing this subtitle. */
    val releaseName: String,
    /** Human-readable language, e.g. "English", "French". */
    val language: String,
    /** Who uploaded this subtitle. */
    val author: String,
    /**
     * Relative URL path used to download the subtitle archive from SubDL's CDN.
     * Passed to [dev.dogetime.data.SubdlApi.download] — no API key is sent on this request.
     */
    val downloadUrl: String,
    /** Whether this subtitle is for the hearing-impaired (SDH). */
    val hearingImpaired: Boolean = false,
    val season: Int? = null,
    val episode: Int? = null,
)

/** Subtitles grouped by language, for display in the UI. */
data class SubtitleGroup(
    val language: String,
    val subtitles: List<Subtitle>,
)
