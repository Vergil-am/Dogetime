package com.example.dogetime.data.remote.dto

data class RequestTokenDTO(
    val expires_at: String,
    val request_token: String,
    val success: Boolean
)