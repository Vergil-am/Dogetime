package com.example.kotlinmovieapp.domain.use_case.auth

import android.util.Log
import com.example.kotlinmovieapp.data.remote.dto.AccountDTO
import com.example.kotlinmovieapp.data.remote.dto.RequestTokenDTO
import com.example.kotlinmovieapp.data.remote.dto.SessionDTO
import com.example.kotlinmovieapp.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.IOException
import retrofit2.HttpException
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val repo: AuthRepository
) {
    fun generateReqToken(): Flow<RequestTokenDTO>  = flow{
            try {
                val res = repo.generateToken()
                emit(res)
            }catch (e : HttpException) {
                Log.e("MOVIE REPO", e.toString() )
            } catch (e: IOException) {
                Log.e("MOVIE REPO", e.toString() )

            }
    }

    fun createSessionId(token: String): Flow<SessionDTO> = flow {
        try {
            val res = repo.getSessionId(token)
            Log.e("SESSION ID", res.toString())
            emit(res)
        }catch (e : HttpException) {
            Log.e("Auth REPO Session", e.toString() )
        } catch (e: IOException) {
            Log.e("Auth REPO Session", e.toString() )

        }
    }

    fun getAccount(sessionId: String): Flow<AccountDTO> = flow {
        try {
            val res = repo.getAccount(sessionId)
            Log.e("ACCOUNT", res.toString())
            emit(res)
        }catch (e : HttpException) {
            Log.e("Auth REPO Account", e.toString() )
        } catch (e: IOException) {
            Log.e("Auth REPO Account", e.toString() )

        }
    }
}