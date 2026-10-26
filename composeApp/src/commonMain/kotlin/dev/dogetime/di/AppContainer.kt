package dev.dogetime.di

import dev.dogetime.data.FavoritesRepository
import dev.dogetime.data.MediaRepository
import dev.dogetime.data.TmdbApi
import dev.dogetime.data.createHttpClient
import dev.dogetime.data.local.createDatabase

/** Manual dependency container. Created once in App() and passed down to the screens. */
class AppContainer {
    val repository: MediaRepository = MediaRepository(TmdbApi(createHttpClient()))
    val favorites: FavoritesRepository = FavoritesRepository(createDatabase().favoriteDao())
}
