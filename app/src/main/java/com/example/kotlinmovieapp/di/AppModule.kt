package com.example.kotlinmovieapp.di

import com.example.kotlinmovieapp.data.remote.MoviesAPI
import com.example.kotlinmovieapp.data.repository.MoviesRepositoryImplementation
import com.example.kotlinmovieapp.domain.repository.MoviesRepo
import com.example.kotlinmovieapp.domain.use_case.movies.GetMovies
import com.example.kotlinmovieapp.domain.use_case.movies.MoviesUseCase
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
    fun provideMoviesRepo(
        moviesAPI: MoviesAPI
    ) : MoviesRepo = MoviesRepositoryImplementation(moviesAPI)
    @Provides
    @Singleton
    fun provideMoviesUseCases(
       moviesRepo : MoviesRepo
    ) : MoviesUseCase {
        return MoviesUseCase(
            getMovies = GetMovies(moviesRepo)
        )
    }
}