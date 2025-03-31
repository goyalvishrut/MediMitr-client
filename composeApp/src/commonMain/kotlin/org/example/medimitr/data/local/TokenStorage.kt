package org.example.medimitr.data.local

interface TokenStorage {
    fun saveToken(token: String)

    fun getToken(): String?

    fun clearToken()
}
