package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.WitanimeAPI
import com.example.kotlinmovieapp.domain.repository.WitanimeRepository
import retrofit2.Response
import javax.inject.Inject

class WitanimeRepoImplementation @Inject constructor(
    private val repo : WitanimeAPI
) : WitanimeRepository {
    override suspend fun getSources(slug: String): Response<String> {
        return repo.getSources(slug)
    }

}