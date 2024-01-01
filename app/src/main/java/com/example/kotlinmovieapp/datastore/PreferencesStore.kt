package com.example.kotlinmovieapp.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PrefrencesStore (
    private val context: Context
) {
    companion object {
        private val Context.dataStore : DataStore<Preferences> by preferencesDataStore("preferences")
        val THEME = stringPreferencesKey("theme")
    }

    val getTheme : Flow<String?> = context.dataStore.data.map {
        it[THEME]
    }
    

    suspend fun storeTheme(theme : String) {
        context.dataStore.edit {
            it[THEME] = theme 
        }
    }
    
}