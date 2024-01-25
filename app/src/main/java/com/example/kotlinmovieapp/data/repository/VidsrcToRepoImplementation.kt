package com.example.kotlinmovieapp.data.repository

import com.example.kotlinmovieapp.data.remote.VidsrcToAPI
import com.example.kotlinmovieapp.domain.model.VidSrcSources
import com.example.kotlinmovieapp.domain.model.VidsrcSource
import com.example.kotlinmovieapp.domain.repository.VidsrcToRepository
import retrofit2.Response
import javax.inject.Inject

class VidsrcToRepoImplementation @Inject constructor(
    private val vidsrc: VidsrcToAPI
) : VidsrcToRepository {
    override suspend fun getMovie(url: String): Response<String> {
        return vidsrc.getMovie(url)
    }

    override suspend fun getSources(dataId: String): VidSrcSources {
        return vidsrc.getSources(dataId)
    }

    override suspend fun getSource(sourceId: String): VidsrcSource {
        return vidsrc.getSource(sourceId)
    }

}