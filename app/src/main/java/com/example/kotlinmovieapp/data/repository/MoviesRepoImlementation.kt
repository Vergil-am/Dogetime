package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.MoviesAPI
import com.example.kotlinmovieapp.data.remote.dto.GenresDTO
import com.example.kotlinmovieapp.data.remote.dto.GetShowsDTO
import com.example.kotlinmovieapp.data.remote.dto.MovieDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.MoviesDTO
import com.example.kotlinmovieapp.data.remote.dto.SearchDTO
import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.data.remote.dto.ShowDetailsDTO
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepoImplementation @Inject constructor(
    private val api : MoviesAPI
): MovieRepository {
    // Trending
    override suspend fun getTrending(): MoviesDTO {
        return api.getTrending()
    }
    override suspend fun getTrendingShows(page: Int): GetShowsDTO {
        return api.getTrendingShows(page)
    }

    // Movies
    override suspend fun getMovies(page : Int, catalog: String): MoviesDTO {
        return api.getMovies(catalog, page = page)
    }

    override suspend fun getMovie(movieId: Int): MovieDetailsDTO {
        return api.getMovie(movieId)
    }


    // Shows

    override suspend fun getShows(page: Int, catalog: String): GetShowsDTO {
        return api.getShows(catalog, page = page)
    }
    override suspend fun getShow(showId: Int): ShowDetailsDTO{
        return api.getShow(showId)
    }
    override suspend fun getSeason(seasonId : Int, season: Int): SeasonDTO {
       return api.getSeason(id = seasonId, season = season)
    }

    // Search
    override suspend fun searchMovies(query: String): SearchDTO {
        return api.searchMovies(query)
    }

    override suspend fun searchShows(query: String): GetShowsDTO {
        return api.searchShows(query)
    }

    override suspend fun getGenres(type: String): GenresDTO {
        return api.getGenres(type)
    }

}