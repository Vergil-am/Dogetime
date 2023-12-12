package com.example.kotlinmovieapp.domain.use_case.animeiat

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeSourcesDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodesDTO
import com.example.kotlinmovieapp.domain.model.Details
import com.example.kotlinmovieapp.domain.model.MovieHome
import com.example.kotlinmovieapp.domain.repository.AnimeiatRepository
import com.example.kotlinmovieapp.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.ByteString.Companion.decodeBase64
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class AnimeiatUseCase @Inject constructor(
    private val repo: AnimeiatRepository
) {
    fun getPopularAnime(query: String?, page: Int?): Flow<List<MovieHome>> = flow {
            try {
                val res = repo.getPopularAnime(query, page).data.map {
                    MovieHome(
                        id = it.id,
                        title = it.anime_name,
                        type = "anime",
                        poster = "${Constants.AIMEIAT_IMAGE_URL}/${it.poster_path}",
                        slug = it.slug
                    )
                }
                Log.e("Animeiat", res.toString())
                emit(res)
            }catch (e : HttpException) {
                Log.e("Animeiat", e.toString() )
            } catch (e: IOException) {
                Log.e("Animeiat", e.toString() )

            }
    }


   fun getLatestEpisodes(): Flow<List<MovieHome>> = flow {
       try {
           val res = repo.getLatestEpisodes().data.map {
               MovieHome(
                   id = it.id,
                   title = it.title,
                   type = "anime",
                   poster = "${Constants.AIMEIAT_IMAGE_URL}/${it.poster_path}",
                   slug = it.slug.substringBefore("-episode-")
               )
           }
           Log.e("Animeiat", res.toString())
           emit(res)
       }catch (e : HttpException) {
           Log.e("Animeiat", e.toString() )
       } catch (e: IOException) {
           Log.e("Animeiat", e.toString() )

       }
   }
    fun getAnimeDetails (slug: String) : Flow<Details> = flow {

        try {
            val res = repo.getAnimeDetails(slug).data
            val anime = Details(
                id = res.id ,
                title = res.anime_name ,
                backdrop = "${Constants.AIMEIAT_IMAGE_URL}/${res.poster_path}",
                poster =  "${Constants.AIMEIAT_IMAGE_URL}/${res.poster_path}",
                genres =  res.genres.map { it.name },
                overview = res.story,
                releaseDate = res.created_at ,
                status = res.status,
                type = "anime",
                episodes = res.total_episodes,
                tagline = null,
                homepage = null,
                lastAirDate = null,
                imdbId = null,
                rating = null,
                runtime = null,
                seasons = null
                )
            emit(anime)
        }catch (e : HttpException) {
            Log.e("Animeiat", e.toString() )
        } catch (e: IOException) {
            Log.e("Animeiat", e.toString() )

        }
    }


    fun getAnimeEpisodes (slug: String) : Flow<AnimeiatEpisodesDTO> = flow  {
        try {
            val res = repo.getEpisodes(slug)
            emit(res)
        }catch (e : HttpException) {
            Log.e("Animeiat", e.toString() )
        } catch (e: IOException) {
            Log.e("Animeiat", e.toString() )

        }
    }

    fun getAnimeEpisodeId(slug: String) : Flow<String> = flow {
        try {
            val res = repo.getEpisode(slug)
            val decoded = res.hash.decodeBase64()?.string(Charsets.UTF_8)?.split("\"")?.reversed()?.get(1)
            if (decoded != null) {
                emit(decoded)
            } else {
                emit("error")
            }
        }catch (e : HttpException) {
            Log.e("Animeiat", e.toString() )
        } catch (e: IOException) {
            Log.e("Animeiat", e.toString() )

        }

    }

    fun getAnimeEpisodeSources(slug : String) : Flow<AnimeiatEpisodeSourcesDTO> = flow {
        try {
            val res = repo.getEpisodeSources(slug)
            emit(res)
        } catch (e : HttpException) {
            Log.e("Animeiat", e.toString() )
        } catch (e: IOException) {
            Log.e("Animeiat", e.toString() )

        }
    }
}