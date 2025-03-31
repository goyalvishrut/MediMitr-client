package org.example.medimitr.domain.auth

import org.example.medimitr.data.api.ApiService
import org.example.medimitr.data.local.TokenStorage

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val tokenStorage: TokenStorage,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): Result<String> =
        try {
            val response = apiService.login(email, password)
            tokenStorage.saveToken(response.token)
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
            tokenStorage.saveToken(response.token)
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override fun isLoggedIn() = tokenStorage.getToken() != null
}
