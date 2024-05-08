package com.example.dogetime.presentation.browse

class Item(
    val title: String,
    val value: String,
)

class Type(
    val title: String, var value: String, val catalog: List<Item>, val genres: List<Genre>
)

val MovieCatalog = listOf(
    Item("Popular", "popular"), Item("Top Rated", "top_rated"), Item("Now playing", "now_playing")
)
val ShowCatalog = listOf(
    Item("Popular", "popular"),
    Item("Top Rated", "top_rated"),
    Item("Airing today", "airing_today"),
    Item("On Air", "on_the_air")
)
val AnimeCatalog = listOf(

    Item("TV", "tv2"),
    Item("Movie", "movie-3"),
    Item("ONA", "ona1"),
    Item("OVA", "ova1"),
    Item("Special", "special1"),
)

val MovieGenres = listOf(
    Genre(28, "Action"),
    Genre(12, "Adventure"),
    Genre(16, "Animation"),
    Genre(35, "Comedy"),
    Genre(80, "Crime"),
    Genre(99, "Documentary"),
    Genre(18, "Drama"),
    Genre(10751, "Family"),
    Genre(14, "Fantasy"),
    Genre(36, "History"),
    Genre(27, "Horror"),
    Genre(10402, "Music"),
    Genre(9648, "Mystery"),
    Genre(10749, "Romance"),
    Genre(878, "Science Fiction"),
    Genre(10770, "TV Movie"),
    Genre(53, "Thriller"),
    Genre(10752, "War"),
    Genre(37, "Western"),
)

val TvGenres = listOf(
    Genre(10759, "Action & Adventure"),
    Genre(16, "Animation"),
    Genre(16, "Animation"),
    Genre(35, "Comedy"),
    Genre(80, "Crime"),
    Genre(99, "Documentary"),
    Genre(18, "Drama"),
    Genre(10751, "Family"),
    Genre(10762, "Kids"),
    Genre(9648, "Mystery"),
    Genre(10763, "News"),
    Genre(10764, "Reality"),
    Genre(10765, "Sci-Fi & Fantasy"),
    Genre(10766, "Soap"),
    Genre(10767, "Talk"),
    Genre(10768, "War & Politics"),
    Genre(37, "Western"),
)

val animeGenres = listOf(
    Genre(null, "أطفال"),
    Genre(null, "أكشن"),
    Genre(null, "إيتشي"),
    Genre(null, "اثارة"),
    Genre(null, "العاب"),
    Genre(null, "بوليسي"),
    Genre(null, "تاريخي"),
    Genre(null, "جنون"),
    Genre(null, "جوسي"),
    Genre(null, "حربي"),
    Genre(null, "حريم"),
    Genre(null, "خارق للعادة"),
    Genre(null, "خيال علمي"),
    Genre(null, "دراما"),
    Genre(null, "رعب"),
    Genre(null, "رومانسي"),
    Genre(null, "رياضي"),
    Genre(null, "ساموراي"),
    Genre(null, "سباق"),
    Genre(null, "سحر"),
    Genre(null, "سينين"),
    Genre(null, "شريحة من الحياة"),
    Genre(null, "شوجو"),
    Genre(null, "شوجو اَي"),
    Genre(null, "شونين"),
    Genre(null, "شونين اي"),
    Genre(null, "شياطين"),
    Genre(null, "غموض"),
    Genre(null, "فضائي"),
    Genre(null, "فنتازيا"),
    Genre(null, "فنون تعبيرية"),
    Genre(null, "فنون قتالية"),
    Genre(null, "قوى خارقة"),
    Genre(null, "كوميدي"),
    Genre(null, "محاكاة ساخرة"),
    Genre(null, "مدرسي"),
    Genre(null, "مصاصي دماء"),
    Genre(null, "مغامرات"),
    Genre(null, "موسيقي"),
    Genre(null, "ميكا"),
    Genre(null, "نفسي")
)
val Types = listOf(
    Type("Movies", "movie", MovieCatalog, MovieGenres),
    Type("Shows", "tv", ShowCatalog, TvGenres),
    Type("Anime - AR", "animeAR", AnimeCatalog, animeGenres),
    Type("Anime - EN", "animeEN", AnimeCatalog, animeGenres),
    Type("Anime - FR", "animeFR", AnimeCatalog, animeGenres)
)

data class Genre(
    val id: Int?, val name: String
)
