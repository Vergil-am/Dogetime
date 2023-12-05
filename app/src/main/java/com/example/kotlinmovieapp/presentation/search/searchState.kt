package com.example.kotlinmovieapp.presentation.search

import com.example.kotlinmovieapp.data.remote.dto.SearchDTO

data class SearchState(
    var search: String = "",
    var movies: SearchDTO? = null,
    var shows : SearchDTO? = null
)
