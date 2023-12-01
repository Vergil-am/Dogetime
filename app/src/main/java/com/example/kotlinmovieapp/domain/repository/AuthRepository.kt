package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.data.remote.dto.AccountDTO
import com.example.kotlinmovieapp.data.remote.dto.RequestTokenDTO
import com.example.kotlinmovieapp.data.remote.dto.SessionDTO

interface AuthRepository {
    suspend fun generateToken() : RequestTokenDTO

    suspend fun getSessionId(token: String): SessionDTO

    suspend fun getAccount(sessionId: String): AccountDTO
}