package com.example.kotlinmovieapp.data.remote

import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatDetailsDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodeSourcesDTO
import com.example.kotlinmovieapp.data.remote.dto.AnimeiatEpisodesDTO
import retrofit2.http.GET
import retrofit2.http.Path

interface AnimeiatAPI {

    @GET("anime")
   suspend fun getPopularAnime() : AnimeiatDTO
   @GET("anime/{slug}")
   suspend fun getAnimeDetails(
      @Path("slug") slug: String
   ) : AnimeiatDetailsDTO

   @GET("anime/{slug}/episodes")
   suspend fun getEpisodes(
       @Path("slug") slug: String
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