package com.example.kotlinmovieapp.presentation.account

data class accountState(
    val token : String? = null,
    val sessionId: String? = null,
    var browserOpened : Boolean = false
)
