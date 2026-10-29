package dev.dogetime.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Raw OpenSubtitles REST API v1 responses and requests.
// Unknown fields are ignored by the JSON configuration in TmdbApi.kt.

@Serializable
data class OpenSubtitlesSearchResponse(
    @SerialName("total_pages") val totalPages: Int = 0,
    @SerialName("total_count") val totalCount: Int = 0,
    @SerialName("per_page") val perPage: Int = 0,
    val page: Int = 0,
    val data: List<OpenSubtitlesData> = emptyList(),
)

@Serializable
data class OpenSubtitlesData(
    val id: String = "",
    val type: String = "",
    val attributes: OpenSubtitlesAttributes = OpenSubtitlesAttributes(),
)

@Serializable
data class OpenSubtitlesAttributes(
    @SerialName("subtitle_id") val subtitleId: String = "",
    val language: String = "",
    @SerialName("download_count") val downloadCount: Int = 0,
    @SerialName("new_download_count") val newDownloadCount: Int = 0,
    @SerialName("hearing_impaired") val hearingImpaired: Boolean = false,
    val hd: Boolean = false,
    val fps: Double? = null,
    val release: String = "",
    val comments: String? = null,
    val url: String = "",
    val uploader: OpenSubtitlesUploader? = null,
    @SerialName("feature_details") val featureDetails: OpenSubtitlesFeatureDetails? = null,
    val files: List<OpenSubtitlesFile> = emptyList(),
)

@Serializable
data class OpenSubtitlesUploader(
    @SerialName("uploader_id") val uploaderId: Long? = null,
    val name: String = "",
    val rank: String? = null,
)

@Serializable
data class OpenSubtitlesFeatureDetails(
    @SerialName("feature_id") val featureId: Long? = null,
    @SerialName("feature_type") val featureType: String? = null,
    val year: Int? = null,
    val title: String? = null,
    @SerialName("movie_name") val movieName: String? = null,
    @SerialName("imdb_id") val imdbId: Long? = null,
    @SerialName("tmdb_id") val tmdbId: Long? = null,
    @SerialName("season_number") val seasonNumber: Int? = null,
    @SerialName("episode_number") val episodeNumber: Int? = null,
)

@Serializable
data class OpenSubtitlesFile(
    @SerialName("file_id") val fileId: Long = 0,
    @SerialName("cd_number") val cdNumber: Int? = null,
    @SerialName("file_name") val fileName: String = "",
)

@Serializable
data class OpenSubtitlesDownloadRequest(
    @SerialName("file_id") val fileId: Long,
    @SerialName("sub_format") val subFormat: String? = null,
)

@Serializable
data class OpenSubtitlesDownloadResponse(
    val link: String = "",
    @SerialName("file_name") val fileName: String = "",
    val requests: Int? = null,
    val remaining: Int? = null,
    val message: String? = null,
    @SerialName("reset_time") val resetTime: String? = null,
)
