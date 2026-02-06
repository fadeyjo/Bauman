package com.example.data_provider_app.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

object UserPreferences {
    private val EMAIL_KEY = stringPreferencesKey("email")

    suspend fun saveEmail(context: Context, email: String) {
        context.dataStore.edit { prefs ->
            prefs[EMAIL_KEY] = email
        }
    }

    suspend fun clearEmail(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(EMAIL_KEY)
        }
    }

    fun getEmail(context: Context): Flow<String?> {
        return context.dataStore.data
            .map { prefs ->
                prefs[EMAIL_KEY]
            }
    }
}
