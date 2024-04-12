package com.example.dogetime.domain.repository

import com.example.dogetime.domain.model.Details
import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.util.Resource
import kotlinx.coroutines.flow.Flow

interface MyCimaRepository {
    suspend fun getLatest(): Flow<Resource<List<MovieHome>>>
    suspend fun getDetails(id: String): Flow<Resource<Details>>
}