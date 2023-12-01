package com.example.kotlinmovieapp.presentation.account

import com.example.kotlinmovieapp.data.remote.dto.AccountDTO

data class accountState(
    var token : String? = null,
    val sessionId: String? = null,
    var browserOpened : Boolean = false,
    var account: AccountDTO? = null
)
