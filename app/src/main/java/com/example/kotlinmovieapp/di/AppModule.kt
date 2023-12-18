package com.example.kotlinmovieapp.di

import android.app.Application
import androidx.room.Room
import com.example.kotlinmovieapp.data.remote.AnimeiatAPI
import com.example.kotlinmovieapp.data.remote.AuthAPI
import com.example.kotlinmovieapp.data.remote.MoviesAPI
import com.example.kotlinmovieapp.data.repository.AnimeiatRepoImplementation
import com.example.kotlinmovieapp.data.repository.AuthRepoImplementation
import com.example.kotlinmovieapp.data.repository.MovieRepoImplementation
import com.example.kotlinmovieapp.domain.repository.AnimeiatRepository
import com.example.kotlinmovieapp.domain.repository.AuthRepository
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import com.example.kotlinmovieapp.local.dao.WatchListDAO
import com.example.kotlinmovieapp.local.database.ListDatabase
import com.example.kotlinmovieapp.local.repository.WatchListRepository
import com.example.kotlinmovieapp.local.repository.WatchListRepositoryImpl
import com.example.kotlinmovieapp.util.Constants.ANIMEIAT_BASE_URL
import com.example.kotlinmovieapp.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule { @Provides
    @Singleton
    fun provideMoviesAPI() : MoviesAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MoviesAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideMovieRepository(api: MoviesAPI) : MovieRepository {
        return MovieRepoImplementation(api)
    }


    @Provides
    @Singleton
    fun provideAuthAPI() : AuthAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepo(api: AuthAPI): AuthRepository {
        return AuthRepoImplementation(api)
    }
//
//    @Provides
//    @Singleton
//    fun provideListAPI(): ListAPI {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(ListAPI::class.java)
//    }
//
//    @Provides
//    @Singleton
//    fun provideListRepo(api: ListAPI): ListRepository {
//        return ListRepoImplementation(api)
//    }


    @Provides
    @Singleton
    fun provideAnimeiatAPI(): AnimeiatAPI {
        return Retrofit.Builder()
            .baseUrl(ANIMEIAT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AnimeiatAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideAnimeiatRepo(api:AnimeiatAPI) : AnimeiatRepository {
        return AnimeiatRepoImplementation(api)
    }

    // Room Database
    @Provides
    @Singleton
    fun provideDatabase(application: Application): ListDatabase {
        return Room.databaseBuilder(
            application,
            ListDatabase::class.java,
            "watchlist_database")
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
    fun provideWatchListRepo(dao: WatchListDAO) : WatchListRepository {
        return WatchListRepositoryImpl(dao)
    }

}
