package com.example.dogetime.data.remote.dto

data class SessionDTO(
    val status_code: Int,
    val status_message: String?,
    val success: Boolean,
    val session_id: String
)