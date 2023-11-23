package com.example.kotlinmovieapp.domain.repository

import com.example.kotlinmovieapp.data.remote.dto.RequestTokenDTO

interface AuthRepository {
    suspend fun generateToken() : RequestTokenDTO
}