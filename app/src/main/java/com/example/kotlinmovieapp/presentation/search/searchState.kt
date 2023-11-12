package com.example.kotlinmovieapp.presentation.search

import com.example.kotlinmovieapp.data.remote.dto.SearchDTO

data class SearchState(
    var search: String = "",
    val result: SearchDTO? = null
)
