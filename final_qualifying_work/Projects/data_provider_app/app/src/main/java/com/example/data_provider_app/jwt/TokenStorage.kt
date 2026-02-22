package com.example.data_provider_app.jwt

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object TokenStorage {

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    }

    fun getAccessToken(): String? =
        prefs.getString("access_token", null)

    fun getRefreshToken(): String? =
        prefs.getString("refresh_token", null)

    fun saveTokens(access: String, refresh: String) {
        prefs.edit {
            putString("access_token", access)
                .putString("refresh_token", refresh)
        }
    }

    fun clear() {
        prefs.edit { clear() }
    }
}
