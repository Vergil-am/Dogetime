package com.example.dogetime.data.remote.dto

import com.example.dogetime.domain.model.Avatar

data class AccountDTO(
    val avatar: Avatar,
    val id: Int,
    val include_adult: Boolean,
    val iso_3166_1: String,
    val iso_639_1: String,
    val name: String,
    val username: String
)