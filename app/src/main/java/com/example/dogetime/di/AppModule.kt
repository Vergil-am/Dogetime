package com.example.dogetime.di

import android.app.Application
import androidx.room.Room
import com.example.dogetime.data.local.dao.WatchListDAO
import com.example.dogetime.data.local.database.ListDatabase
import com.example.dogetime.data.remote.Anime4upAPI
import com.example.dogetime.data.remote.AnimeCatAPI
import com.example.dogetime.data.remote.CimaLekAPI
import com.example.dogetime.data.remote.GogoAnimeAPI
import com.example.dogetime.data.remote.MoviesAPI
import com.example.dogetime.data.remote.WitanimeAPI
import com.example.dogetime.data.repository.Anime4upRepoImplementation
import com.example.dogetime.data.repository.AnimeCatRepoImplementation
import com.example.dogetime.data.repository.CimaLekRepoImplementation
import com.example.dogetime.data.repository.GogoAnimeRepoIementation
import com.example.dogetime.data.repository.MovieRepoImplementation
import com.example.dogetime.data.repository.WatchListRepositoryImpl
import com.example.dogetime.data.repository.WitanimeRepoImplementation
import com.example.dogetime.domain.repository.Anime4upRepository
import com.example.dogetime.domain.repository.AnimeCatRepository
import com.example.dogetime.domain.repository.CimaLekRepository
import com.example.dogetime.domain.repository.GogoAnimeRepository
import com.example.dogetime.domain.repository.MovieRepository
import com.example.dogetime.domain.repository.WatchListRepository
import com.example.dogetime.domain.repository.WitanimeRepository
import com.example.dogetime.util.Constants.ANIME4UP_URL
import com.example.dogetime.util.Constants.ANIMECAT_URL
import com.example.dogetime.util.Constants.BASE_URL
import com.example.dogetime.util.Constants.CIMALEK_URL
import com.example.dogetime.util.Constants.GOGOANIME_URL
import com.example.dogetime.util.Constants.WITANIME_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideMoviesAPI(): MoviesAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(api: MoviesAPI): MovieRepository {
        return MovieRepoImplementation(api)
    }


    // Room Database
    @Provides
    @Singleton
    fun provideDatabase(application: Application): ListDatabase {
        return Room.databaseBuilder(
            application,
            ListDatabase::class.java,
            "watchlist_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideWatchListDao(db: ListDatabase): WatchListDAO {
        return db.watchListDao()
    }

    @Provides
    @Singleton
    fun provideWatchListRepo(dao: WatchListDAO): WatchListRepository {
        return WatchListRepositoryImpl(dao)
    }

    // anime4up
    @Provides
    @Singleton
    fun provideAnime4upAPI(): Anime4upAPI {
        return Retrofit.Builder()
            .baseUrl(ANIME4UP_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(Anime4upAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideAnime4upRepo(api: Anime4upAPI): Anime4upRepository {
        return Anime4upRepoImplementation(api)
    }

    @Provides
    @Singleton
    fun provideWitanimeAPI(): WitanimeAPI {
        return Retrofit.Builder()
            .baseUrl(WITANIME_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(WitanimeAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideWitanimeRepo(api: WitanimeAPI): WitanimeRepository {
        return WitanimeRepoImplementation(api)
    }

    // GOGO anime
    @Provides
    @Singleton
    fun provideGogoAnimeAPI(): GogoAnimeAPI {
        return Retrofit.Builder()
            .baseUrl(GOGOANIME_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(GogoAnimeAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideGogoAnimeRepo(api: GogoAnimeAPI): GogoAnimeRepository {
        return GogoAnimeRepoIementation(api)
    }


    // Anime cat
    @Provides
    @Singleton
    fun provideAnimeCatAPI(): AnimeCatAPI {
        return Retrofit.Builder()
            .baseUrl(ANIMECAT_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(AnimeCatAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideAnimeCatRepo(api: AnimeCatAPI): AnimeCatRepository {
        return AnimeCatRepoImplementation(api)
    }

    // Cima lek
    @Provides
    @Singleton
    fun provideCimaLekAPi(): CimaLekAPI {
        return Retrofit.Builder()
            .baseUrl(CIMALEK_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(CimaLekAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideCimaLekRepo(api: CimaLekAPI): CimaLekRepository {
        return CimaLekRepoImplementation(api)
    }
}
