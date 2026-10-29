package dev.dogetime.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Raw SubDL API v2 responses. Only the fields the app uses are declared;
// unknown fields are ignored by the JSON configuration in TmdbApi.kt.

@Serializable
data class SubdlSearchResponse(
    val status: Boolean = false,
    val results: List<SubdlResult> = emptyList(),
    val subtitles: List<SubdlSubtitle> = emptyList(),
)

@Serializable
data class SubdlResult(
    @SerialName("sd_id") val sdId: Int? = null,
    val type: String? = null,
    val name: String? = null,
    @SerialName("imdb_id") val imdbId: String? = null,
    val year: Int? = null,
)

@Serializable
data class SubdlSubtitle(
    @SerialName("release_name") val releaseName: String = "",
    val name: String = "",
    val lang: String = "",
    val author: String = "",
    val url: String = "",
    @SerialName("subtitlePage") val subtitlePage: String? = null,
    val season: Int? = null,
    val episode: Int? = null,
    val language: String = "",
    @SerialName("hi") val hearingImpaired: Boolean = false,
    @SerialName("full_season") val fullSeason: Boolean = false,
)
