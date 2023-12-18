package com.example.kotlinmovieapp.domain.use_case.movies.get_movie

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.SeasonDTO
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.repository.MovieRepository
import com.example.kotlinmovieapp.util.Constants
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieUseCase @Inject constructor(
    private val repo : MovieRepository
) {
    fun getMovieDetails(id: Int): Flow<Details> = flow {
        try {
            val res = repo.getMovie(movieId = id)
            val movie = Details(
                id = res.id ,
                title = res.title,
                backdrop = "${Constants.IMAGE_BASE_URL}/w500/${res.backdrop_path}",
                poster =  "${Constants.IMAGE_BASE_URL}/w200/${res.poster_path}" ,
                genres =  res.genres.map { it.name },
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
                slug = res.title.replace(" ", "-")
            )
            Log.d("MOVIE REPO", movie.toString())
            emit(movie)
        } catch (e : HttpException) {
            Log.e("MOVIE REPO", e.toString() )
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString() )

        }
    }
    fun getShow(id: Int): Flow<Details> = flow {
        try {
            val  res = repo.getShow(id)
            val runtime = res.episode_run_time.firstOrNull()
            Log.e("RUNTIME", runtime.toString())
            val episodeRunTime = if (runtime == null) {
                if (res.origin_country[0] == "JP" || res.genres[0].name == "Animation") {
                    23
                } else {
                    43
                }
            } else if (runtime is Int) {
                runtime
            } else if (runtime is Double || runtime is Float){
               runtime.toString().substringBefore(".").toInt()
            } else {
                null
            }
            val show = Details(
                id = res.id ,
                title = res.name,
                backdrop = "${Constants.IMAGE_BASE_URL}/w500/${res.backdrop_path}" ,
                poster =  "${Constants.IMAGE_BASE_URL}/w200/${res.poster_path}" ,
                genres =  res.genres.map { it.name },
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
                seasons = res.seasons,
                slug = res.name.replace(" ", "-")
            )
            Log.e("SHOW REPO", show.toString() )
            emit(show)
        }catch (e : HttpException) {
            Log.e("MOVIE REPO", e.toString() )
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString() )

        }
    }

    fun getSeason(id: Int, season: Int): Flow<SeasonDTO> = flow {
        try {
            val res = repo.getSeason(id, season)
            Log.e("SHOW REPO", season.toString() )
            emit(res)
        }catch (e : HttpException) {
            Log.e("MOVIE REPO", e.toString() )
        } catch (e: IOException) {
            Log.e("MOVIE REPO", e.toString() )

        }

    }

}