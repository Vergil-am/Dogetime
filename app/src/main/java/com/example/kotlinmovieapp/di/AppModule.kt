package com.example.kotlinmovieapp.di

import com.example.kotlinmovieapp.data.remote.AuthAPI
import com.example.kotlinmovieapp.data.remote.ListAPI
import com.example.kotlinmovieapp.data.remote.MoviesAPI
import com.example.kotlinmovieapp.data.repository.AuthRepoImplementation
import com.example.kotlinmovieapp.data.repository.ListRepoImplementation
import com.example.kotlinmovieapp.data.repository.MovieRepoImplementation
import com.example.kotlinmovieapp.domain.repository.AuthRepository
import com.example.kotlinmovieapp.domain.repository.ListRepository
import com.example.kotlinmovieapp.domain.repository.MovieRepository
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
object AppModule {
    @Provides
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

    @Provides
    @Singleton
    fun provideListAPI(): ListAPI {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ListAPI::class.java)
    }

    @Provides
    @Singleton
    fun provideListRepo(api: ListAPI): ListRepository {
        return ListRepoImplementation(api)
    }
}
