package com.example.dogetime.util

import com.example.dogetime.domain.model.MovieHome
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