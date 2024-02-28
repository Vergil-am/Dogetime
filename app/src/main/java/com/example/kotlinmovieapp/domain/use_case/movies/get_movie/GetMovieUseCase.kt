package com.example.kotlinmovieapp.domain.use_case.movies.get_movie

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.model.Source
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import com.example.kotlinmovieapp.util.Constants
import com.example.kotlinmovieapp.util.Resource
import com.example.kotlinmovieapp.util.extractors.vidsrcme.Vidsrcme
import com.example.kotlinmovieapp.util.extractors.vidsrcto.Vidsrcto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class GetMovieUseCase @Inject constructor(
    private val repo: MovieRepository
) {
    fun getMovieDetails(id: Int): Flow<Resource<Details>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getMovie(movieId = id)
            val movie = Details(
                id = res.id.toString(),
                title = res.title,
                backdrop = "${Constants.IMAGE_BASE_URL}/w500/${res.backdrop_path}",
                poster = "${Constants.IMAGE_BASE_URL}/w200/${res.poster_path}",
                genres = res.genres.map { it.name },
                overview = res.overview,
                releaseDate = res.release_date,
                status = res.status,
                type = "movie",
                episodes = null,
                tagline = res.tagline,
                homepage = res.homepage,
                lastAirDate = null,
                imdbId = res.imdb_id,
                rating = res.vote_average,
                runtime = res.runtime,
                seasons = null,
            )
            emit(Resource.Success(movie))
        } catch (e: HttpException) {
            Log.e("MOVIE REPO", e.toString())
            emit(Resource.Error("HTTP exception"))
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString())
            emit(Resource.Error("IO exception"))
        }
    }

    fun getShow(id: Int): Flow<Resource<Details>> = flow {
        emit(Resource.Loading())
        try {
            val res = repo.getShow(id)
            val runtime = res.episode_run_time.firstOrNull()
            val episodeRunTime = if (runtime == null) {
                if (res.origin_country[0] == "JP" || res.genres[0].name == "Animation") {
                    23
                } else {
                    43
                }
            } else if (runtime is Int) {
                runtime
            } else if (runtime is Double || runtime is Float) {
                runtime.toString().substringBefore(".").toInt()
            } else {
                null
            }
            var show = Details(
                id = res.id.toString(),
                title = res.name,
                backdrop = "${Constants.IMAGE_BASE_URL}/w500/${res.backdrop_path}",
                poster = "${Constants.IMAGE_BASE_URL}/w200/${res.poster_path}",
                genres = res.genres.map { it.name },
                overview = res.overview,
                releaseDate = res.first_air_date,
                status = res.status,
                type = "show",
                episodes = null,
                tagline = res.tagline,
                homepage = res.homepage,
                lastAirDate = res.last_air_date,
                imdbId = null,
                rating = res.vote_average,
                runtime = episodeRunTime,
//                seasons = res.seasons,
                seasons = emptyList()
            )
            emit(Resource.Success(show))
            val seasons = res.seasons.map {
                val season = getSeason(id, it.season_number)
                season
            }

            if (seasons.size == res.seasons.size) {
                Log.e("SEASONS", seasons.toString())
                show = show.copy(seasons = seasons)
                emit(Resource.Success(show))
            }
        } catch (e: HttpException) {
            Log.e("MOVIE REPO", e.toString())
            emit(Resource.Error("http exception"))
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString())
            emit(Resource.Error("IO exception"))
        }
    }

    private suspend fun getSeason(id: Int, season: Int): SeasonDTO {
        try {
            return repo.getSeason(id, season)
        } catch (e: HttpException) {
            Log.e("MOVIE REPO", e.toString())
            e.printStackTrace()
            throw e
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString())
            e.printStackTrace()
            throw e
        }

    }

    fun getSources(id: Int?, type: String, episode: Int?, season: Int?): Flow<List<Source>> = flow {
        val sources = mutableListOf<Source>()

        when (type) {
            "movie" -> {
                sources.addAll(Vidsrcto().getSources("${Constants.VIDSRC_MULTI}/embed/movie/$id"))
                Vidsrcme().getSources("${Constants.VIDSRC_FHD}/embed/movie/$id")
            }

            "show" -> {
                sources.addAll(Vidsrcto().getSources("${Constants.VIDSRC_MULTI}/embed/tv/$id/$season/$episode"))
                Vidsrcme().getSources("${Constants.VIDSRC_FHD}/embed/tv/$id/$season/$episode")
            }
        }

        emit(sources)
    }

}