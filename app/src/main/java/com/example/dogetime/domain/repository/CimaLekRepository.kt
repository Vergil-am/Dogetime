package com.example.dogetime.domain.repository

import com.example.dogetime.domain.model.MovieHome
import com.example.dogetime.util.Resource
import kotlinx.coroutines.flow.Flow

interface CimaLekRepository {
    suspend fun getLatest(): Flow<Resource<List<MovieHome>>>
}