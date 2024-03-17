package com.example.dogetime.data.repository

import com.example.dogetime.data.remote.WitanimeAPI
import com.example.dogetime.domain.repository.WitanimeRepository
import retrofit2.Response
import javax.inject.Inject

class WitanimeRepoImplementation @Inject constructor(
    private val repo : WitanimeAPI
) : WitanimeRepository {
    override suspend fun getSources(slug: String): Response<String> {
        return repo.getSources(slug)
    }

}