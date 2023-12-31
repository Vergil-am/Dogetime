package com.example.kotlinmovieapp.util

import com.example.kotlinmovieapp.domain.model.MovieHome
import org.jsoup.select.Elements

fun parseAnime(
    cards: Elements
) : List<MovieHome> {
   return cards.map {
           MovieHome(
               id = it.selectFirst("a")?.attr("href")?.split("/")?.reversed()?.get(1) ?: "",
               poster = it.selectFirst("img.img-responsive")?.attr("src") ?: "",
               type = "anime",
               title = it.selectFirst("h3")?.text() ?: ""
           )
   }
}