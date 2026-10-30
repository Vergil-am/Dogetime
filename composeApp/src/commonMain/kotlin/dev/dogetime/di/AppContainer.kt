package dev.dogetime.di

import dev.dogetime.data.FavoritesRepository
import dev.dogetime.data.KitsuApi
import dev.dogetime.data.MediaRepository
import dev.dogetime.data.OpenSubtitlesApi
import dev.dogetime.data.SubdlApi
import dev.dogetime.data.TmdbApi
import dev.dogetime.data.TorrentioApi
import dev.dogetime.data.YtsSubsApi
import dev.dogetime.data.createHttpClient
import dev.dogetime.data.local.createDatabase

/** Manual dependency container. Created once in App() and passed down to the screens. */
class AppContainer {
    private val httpClient = createHttpClient()
    val kitsuApi: KitsuApi = KitsuApi(httpClient)
    val repository: MediaRepository = MediaRepository(TmdbApi(httpClient), kitsuApi)
    val torrentio: TorrentioApi = TorrentioApi(httpClient)
    val subdl: SubdlApi = SubdlApi(httpClient)
    val openSubtitles: OpenSubtitlesApi = OpenSubtitlesApi(httpClient)
    val ytsSubs: YtsSubsApi = YtsSubsApi(httpClient)
    val favorites: FavoritesRepository = FavoritesRepository(createDatabase().favoriteDao())
}
