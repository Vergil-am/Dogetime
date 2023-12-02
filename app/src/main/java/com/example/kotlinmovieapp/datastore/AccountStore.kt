package com.example.kotlinmovieapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountStore (
    private val context: Context
) {
    companion object {
        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("account")
        val SESSION_ID_KEY = stringPreferencesKey("session_id")
        val ACCOUNT_ID_KEY = stringPreferencesKey("account_id")
    }

    val getSessionId: Flow<String?> = context.dataStore.data.map {
        it[SESSION_ID_KEY]
    }
    val getAccountID: Flow<String?> = context.dataStore.data.map {
        it[ACCOUNT_ID_KEY]
    }

    suspend fun storeSessionId(sessionId : String) {
        context.dataStore.edit {
            it[SESSION_ID_KEY] = sessionId
        }
    }

    suspend fun storeAccountId(accountId: Int) {
        context.dataStore.edit {
            it[ACCOUNT_ID_KEY] = accountId.toString()
        }

    }

}