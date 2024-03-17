package com.example.dogetime.data.remote.dto

data class AddToWatchListDTO(
    val media_type: String,
    val media_id: Int,
    val watchlist: Boolean
) {

}
