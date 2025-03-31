package org.example.medimitr.domain.auth

interface AuthRepository {
    suspend fun login(
        email: String,
        password: String,
    ): Result<String>

    suspend fun signup(
        name: String,
        password: String,
        email: String,
        phone: String,
        address: String,
    ): Result<User>

    fun isLoggedIn(): Boolean
}
