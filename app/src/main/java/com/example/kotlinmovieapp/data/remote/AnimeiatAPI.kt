package com.example.kotlinmovieapp.data.remote

import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeSourcesDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodesDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatLatestEpisodesDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface AnimeiatAPI {

    @GET("anime")
   suspend fun getPopularAnime(
      @Query("q") query: String?,
      @Query("page") page: Int? = 1
   ) : AnimeiatDTO

   @GET("home/sticky-episodes")
   suspend fun getLatestEpisodes(
      @Query("page") page: Int? = 1
   ): AnimeiatLatestEpisodesDTO
   @GET("anime/{slug}")
   suspend fun getAnimeDetails(
      @Path("slug") slug: String
   ) : AnimeiatDetailsDTO

   @GET("anime/{slug}/episodes")
   suspend fun getEpisodes(
       @Path("slug") slug: String,
       @Query("page") page: Int
   ) : AnimeiatEpisodesDTO

   @GET("episode/{slug}")
   suspend fun getEpisode(
       @Path("slug") slug: String
   ) : AnimeiatEpisodeDTO

   @GET("video/{slug}")
   suspend fun getEpisodeSources(
      @Path("slug") slug: String
   ) : AnimeiatEpisodeSourcesDTO
}