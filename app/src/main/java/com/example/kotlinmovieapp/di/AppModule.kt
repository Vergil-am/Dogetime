package com.example.kotlinmovieapp.di

import android.app.Application
import androidx.room.Room
import com.example.kotlinmovieapp.data.local.dao.WatchListDAO
import com.example.kotlinmovieapp.data.local.database.ListDatabase
import com.example.kotlinmovieapp.data.remote.Anime4upAPI
import com.example.kotlinmovieapp.data.remote.MoviesAPI
import com.example.kotlinmovieapp.data.remote.WitanimeAPI
import com.example.kotlinmovieapp.data.repository.Anime4upRepoImplementation
import com.example.kotlinmovieapp.data.repository.MovieRepoImplementation
import com.example.kotlinmovieapp.data.repository.WatchListRepositoryImpl
import com.example.kotlinmovieapp.data.repository.WitanimeRepoImplementation
import com.example.kotlinmovieapp.domain.repository.Anime4upRepository
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import com.example.kotlinmovieapp.domain.repository.WatchListRepository
import com.example.kotlinmovieapp.domain.repository.WitanimeRepository
import com.example.kotlinmovieapp.util.Constants.ANIME4UP_URL
import com.example.kotlinmovieapp.util.Constants.BASE_URL
import com.example.kotlinmovieapp.util.Constants.WITANIME_URL
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
    fun provideWitanimeAPI(): WitanimeAPI{
        return Retrofit.Builder()
            .baseUrl(WITANIME_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(WitanimeAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideWitanimeRepo(api: WitanimeAPI): WitanimeRepository{
        return WitanimeRepoImplementation(api)
    }
}
