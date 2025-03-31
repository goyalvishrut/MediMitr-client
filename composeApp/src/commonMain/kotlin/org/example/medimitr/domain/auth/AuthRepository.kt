package org.example.medimitr.domain.auth

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): Result<String>

    suspend fun signup(
        username: String,
        password: String,
        email: String,
    ): Result<String>

    fun isLoggedIn(): Boolean
}
