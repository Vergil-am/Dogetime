package dev.dogetime.data

import dev.dogetime.domain.TorrentOption
import dev.dogetime.domain.WatchTarget
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.encodeURLParameter
import kotlinx.serialization.Serializable

/** Stremio add-on torrent providers. */
enum class TorrentProvider(val displayName: String, val streamBaseUrl: String) {
    Torrentio("Torrentio", "https://torrentio.strem.fun/stream"),
    Meteor("Meteor", "https://meteorfortheweebs.midnightignite.me/eyJjYWNoZWRPbmx5IjpmYWxzZSwic2tpcFJlbGVhc2VGaWx0ZXIiOnRydWUsInJlbW92ZVRyYXNoIjpmYWxzZSwicmVtb3ZlU2FtcGxlcyI6ZmFsc2UsImFsbG93QWR1bHQiOnRydWUsImV4Y2x1ZGUzRCI6ZmFsc2UsImVuYWJsZVNlYURleCI6ZmFsc2UsInNob3dZb3VyTWVkaWEiOmZhbHNlLCJ5b3VyTWVkaWFMZWdhY3lNb2RlIjpmYWxzZSwibWluU2VlZGVycyI6MCwibWF4UmVzdWx0cyI6MCwibWF4UGVyUmVzb2x1dGlvbiI6MCwicmVzb2x1dGlvbnMiOltdLCJwcmVmZXJyZWRMYW5ncyI6WyJlbiIsIm11bHRpIiwiYXIiXSwibGFuZ3VhZ2VzIjpbXSwiZXhjbHVkZWRMYW5ncyI6W10sInJlc3VsdEZvcm1hdCI6WyJ0aXRsZSIsInF1YWxpdHkiLCJzaXplIiwiYXVkaW8iLCJzdWJsYW5nIiwic2VlZGVycyJdLCJsYW5ndWFnZUZvcm1hdCI6ImZsYWdzIiwic29ydE9yZGVyIjpbInNlZWRlcnMiLCJjYWNoZWQiLCJzZWFkZXgiLCJyZXNvbHV0aW9uIiwicXVhbGl0eSIsImxhbmd1YWdlIiwic2l6ZSIsInBhY2siLCJoZHIiLCJjb2RlYyJdLCJhbGxvd1AyUCI6dHJ1ZSwiZXhjbHVkZWRTb3VyY2VzIjpbXX0/stream"),
    MediaFusion("MediaFusion", "https://mediafusionfortheweebs.midnightignite.me/D-HUufCDQWU_edY4p0pvlv3bW-kT1_rjAObwDiaW_gVmYS65NP87uSq5iujP9JDcc7m82or13NlaPFYRxXednWPW-fXFvfmHK5H0QXZosKEz8F5iuzsMQsnqKo6WVN8ZCuoGSjpK7KyjWXF2TTXQ0bQx1ILQ5xQdDAxrP-dhLogEO0ssOEJbemPch-nb_KkmeEjKOJil4MmD2XCnNPDO4_I6qI9hmbukBaTx4yCwThh1iu-SDoMpI8U9RDD7OWebbptqEtmggHAP8LQjIsQAWdqwkPfmlAD0R6K46zOY3Ow2dekFZsZ65exLgm5704CjV3srnje4i084YBL1bVUvAgkHjvrLiNY4zdtl60_4o6M2D2q9PIGkGsqUrTDWzbHvrugNTFt8_HooB3svJAXp2pgjXOdNv0xuYW26_iEDxy5ksvxUkUqlGAOjG9-GmQHprwxQ3I5HSe7vgETrz-DV1e8erToAp4eAgqS2LBJBqrJkn8KaW8dndbtA8_X_86hWQUmz5M3TDGW71SvLeh15ZXNfcvNU9DbfMhM4wF1_-5mNAi20-be5rCYIAocmvFv4Q7R6KugmokEsic_Zvpv_tdmLhWRwSO-uSGKgAja-n44kuUZoca1UeqCQhry7sJer_85ezXpnQdVXL0Is4e3KkdqrNtNOhNqPyWbOyFMbmxLLiHegALwgL1QRWMlxpmCcnSZgFnARYs4rVMn15t620EfbHWGypLlnhOivVIbxfCHsZv-QuSoeM4_LIHYLZMvT9psePaDMHrCupmONjdpqCApYY5Bha4g_oc2Ip0uEPYdXpuxSc2Iw8_8FbBw2OJRF3gWX65pqypmzspqa0Hki-s6Bslc1PLAjmhr8u4VSxJShU8mFokj_shYTdQyjYGfzn1lPA8oSBJUprTMnG8ynDQdB9jpcAtIH3Nfnp9n3dxgv6QmVW9aC4R-fTcnrHeVV40sIw8BNu9ype1Kt7SMLdeP97Q0gYSc4m_r46Z2EC1_WBbJaApjmln8oMdahDTzxMRVrPAOPSe8bEtED2Qs1jxX5pzs6Qa0MzfOLRRPvLX33JDFybOnvFm6rwrRVRFW_UsgNEtSzdgymFMH04m1aQFCzrNk5btYa8yr5VqzolkT5PNIkjjDCGiuPkhVCAPDx7q_ranU5xDl4wNbvr7KHgYSEErRX0iuHgtvb_OkOBRPUAUcvmsWAZvHwyVtZMxYePbjGwnUqVjsyjL3rcUTl0HCzbFsuObFAy-AYFRhlPj-JZQijvcoYjEPPnLeZolIThCrnfFInc-qijhRY0wECVujyscUI0Y4-nb5PUF4H_IJMiXeSqF0a2MefAwHgtgAIegfnkWaglgnypEc7kvzfZtFn8P-MfzA8RGCZr6ZtNAJPLRd6H9asrg4-BeuKCPPS8_e9Vz-3EfusaIDf3ynIeIBx967UlosPcl-44IHPAORasIrU22r74Oa-AIA5rPCuQFGsgD93A4e_oyKDALNxaiPg8yp4FlZDvpUtMd8iC0yDmAb9OJyx_NtLfsOselZpWbm9HfKkukewQdnqywLRl9tnVH1P70P3_1tICtv8z-p8Q/stream"),
    Comet("Comet", "https://comet.feels.legal/eyJtYXhSZXN1bHRzUGVyUmVzb2x1dGlvbiI6MTAsIm1heFNpemUiOjIxNDc0ODM2NDgwLCJjYWNoZWRPbmx5IjpmYWxzZSwic29ydENhY2hlZFVuY2FjaGVkVG9nZXRoZXIiOmZhbHNlLCJyZW1vdmVUcmFzaCI6dHJ1ZSwicmVzdWx0Rm9ybWF0IjpbImFsbCJdLCJkZWJyaWRTZXJ2aWNlcyI6W10sImVuYWJsZVRvcnJlbnQiOnRydWUsImRlZHVwbGljYXRlU3RyZWFtcyI6ZmFsc2UsInNjcmFwZURlYnJpZEFjY291bnRUb3JyZW50cyI6ZmFsc2UsImRlYnJpZFN0cmVhbVByb3h5UGFzc3dvcmQiOiIiLCJsYW5ndWFnZXMiOnsicmVxdWlyZWQiOltdLCJhbGxvd2VkIjpbXSwiZXhjbHVkZSI6W10sInByZWZlcnJlZCI6W119LCJyZXNvbHV0aW9ucyI6eyJyMjE2MHAiOmZhbHNlfSwib3B0aW9ucyI6eyJyZW1vdmVfcmFua3NfdW5kZXIiOi0xMDAwMDAwMDAwMCwiYWxsb3dfZW5nbGlzaF9pbl9sYW5ndWFnZXMiOmZhbHNlLCJyZW1vdmVfdW5rbm93bl9sYW5ndWFnZXMiOmZhbHNlfX0=/stream"),
}

// Well-known public trackers find peers faster than DHT alone.
private val TRACKERS = listOf(
    "udp://tracker.opentrackr.org:1337/announce",
    "udp://open.demonii.com:1337/announce",
    "udp://open.stealth.si:80/announce",
    "udp://tracker.torrent.eu.org:451/announce",
    "udp://exodus.desync.com:6969/announce",
    "udp://tracker.openbittorrent.com:6969/announce",
)

/** Lists torrents for a movie or episode by its IMDb id using Stremio add-on providers. */
class TorrentioApi(private val client: HttpClient) {

    /** Torrents for [target] from [provider], best quality first. Throws if [target] has no IMDb id. */
    suspend fun streams(
        target: WatchTarget,
        provider: TorrentProvider = TorrentProvider.Torrentio,
    ): List<TorrentOption> {
        val imdbId = requireNotNull(target.imdbId) { "This title has no IMDb id, which torrents are looked up by." }
        val path = when (target) {
            is WatchTarget.Movie -> "movie/$imdbId.json"
            is WatchTarget.Episode -> "series/$imdbId:${target.season}:${target.episode}.json"
        }
        val response: TorrentioResponse = client.get("${provider.streamBaseUrl}/$path").body()
        return response.streams.mapNotNull { it.toDomain() }
    }
}

@Serializable
private data class TorrentioResponse(val streams: List<TorrentioStream> = emptyList())

@Serializable
private data class TorrentioStream(
    val name: String = "",
    val title: String? = null,
    val description: String? = null,
    val infoHash: String? = null,
    val fileIdx: Int? = null,
    val sources: List<String> = emptyList(),
    val behaviorHints: TorrentioHints? = null,
)

@Serializable
private data class TorrentioHints(
    val filename: String? = null,
    val seeders: Int? = null,
)

private val SEEDERS = Regex("""[👤👥]\s*(\d+)""")
private val SIZE = Regex("""💾\s*([\d.,]+\s*[KMGT]?i?B)""")
private val SITE = Regex("""[⚙🔎🏷]\uFE0F?\s*(.+)""")
private val RESOLUTION_REGEX = Regex("""(?i)\b(2160p|4k|1080p|720p|576p|480p|360p|240p)\b""")

private fun TorrentioStream.toDomain(): TorrentOption? {
    val hash = infoHash?.takeIf { it.isNotBlank() } ?: return null
    val rawText = (title ?: description).orEmpty()
    val lines = rawText.lines().map { it.trim() }.filter { it.isNotEmpty() }
    val stats = lines.lastOrNull { "👤" in it || "👥" in it || "💾" in it }.orEmpty()
    val fileName = behaviorHints?.filename
    val release = fileName ?: lines.firstOrNull { it != stats }?.removePrefix("📄")?.trim() ?: hash

    val streamTrackers = sources
        .filter { it.startsWith("tracker:") }
        .map { it.removePrefix("tracker:") }
    val allTrackers = (streamTrackers + TRACKERS).distinct()

    val magnet = buildString {
        append("magnet:?xt=urn:btih:").append(hash)
        append("&dn=").append((fileName ?: release).encodeURLParameter())
        allTrackers.forEach { append("&tr=").append(it.encodeURLParameter()) }
    }

    val quality = run {
        if ('\n' in name) {
            val after = name.substringAfter('\n').trim()
            if (after.isNotEmpty()) return@run after
        }
        RESOLUTION_REGEX.find(name)?.value?.let { return@run it }
        RESOLUTION_REGEX.find(rawText)?.value?.let { return@run it }
        "Unknown"
    }

    return TorrentOption(
        quality = quality,
        release = release,
        seeders = SEEDERS.find(rawText)?.groupValues?.get(1)?.toIntOrNull() ?: behaviorHints?.seeders,
        size = SIZE.find(rawText)?.groupValues?.get(1),
        site = SITE.find(rawText)?.groupValues?.get(1)?.trim(),
        magnet = magnet,
        fileIndex = fileIdx,
        fileName = fileName,
    )
}
