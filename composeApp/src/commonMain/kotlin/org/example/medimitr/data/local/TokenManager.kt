package org.example.medimitr.data.local

import com.russhwolf.settings.Settings

class TokenManager(
    private val settings: Settings,
) {
    fun saveToken(token: String) {
        settings.putString(TOKEN_KEY, token)
    }

    fun getToken(): String? = settings.getStringOrNull(TOKEN_KEY)

    fun clearToken() {
        settings.remove(TOKEN_KEY)
    }

    companion object {
        private const val TOKEN_KEY = "jwt_token"
    }
}
