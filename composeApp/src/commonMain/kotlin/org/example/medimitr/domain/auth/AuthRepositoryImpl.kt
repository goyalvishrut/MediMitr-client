package org.example.medimitr.domain.auth

import org.example.medimitr.data.api.ApiService

class AuthRepositoryImpl(
    private val apiService: ApiService,
) : AuthRepository {
    private var token: String? = null

    override suspend fun login(
        email: String,
        password: String,
    ): Result<String> =
        try {
            val response = apiService.login(email, password)
            token = response.token
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun signup(
        username: String,
        password: String,
        email: String,
    ): Result<String> =
        try {
            val response = apiService.signup(username, password, email)
            token = response.token
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override fun isLoggedIn() = token != null
}
