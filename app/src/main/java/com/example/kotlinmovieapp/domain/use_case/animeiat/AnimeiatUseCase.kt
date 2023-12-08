package com.example.kotlinmovieapp.domain.use_case.animeiat

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodesDTO
import com.example.kotlinmovieapp.domain.repository.AnimeiatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class AnimeiatUseCase @Inject constructor(
    private val repo: AnimeiatRepository
) {
    fun getPopularAnime(): Flow<AnimeiatDTO> = flow {
            try {
                val res = repo.getPopularAnime()
                Log.e("Animeiat", res.toString())
                emit(res)
            }catch (e : HttpException) {
                Log.e("Animeiat", e.toString() )
            } catch (e: IOException) {
                Log.e("Animeiat", e.toString() )

            }
    }


    fun getAnimeDetails (slug: String) : Flow<AnimeiatDetailsDTO> = flow {

        try {
            val res = repo.getAnimeDetails(slug)
            emit(res)
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
}