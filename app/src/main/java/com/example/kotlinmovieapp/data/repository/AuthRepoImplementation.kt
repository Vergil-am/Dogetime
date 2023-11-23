package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.AuthAPI
import com.example.kotlinmovieapp.data.remote.dto.RequestTokenDTO
import com.example.kotlinmovieapp.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepoImplementation @Inject constructor(
    private val authAPI: AuthAPI
) : AuthRepository{
    override suspend fun generateToken(): RequestTokenDTO {
       return authAPI.generateToken()
    }

}