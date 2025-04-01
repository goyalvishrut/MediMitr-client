package org.example.medimitr.domain.auth

import org.example.medimitr.data.api.ApiService
import org.example.medimitr.data.local.TokenStorage
import org.example.medimitr.data.model.request.NewUserRequest

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val tokenStorage: TokenStorage,
    private val userRepository: UserRepository,
) : AuthRepository {
    override suspend fun login(
        email: String,
        password: String,
    ): Result<String> =
        try {
            val response = apiService.login(email, password)
            tokenStorage.saveToken(response.token)
            userRepository.updateAuthStatus()
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }

    override suspend fun signup(
        name: String,
        password: String,
        email: String,
        phone: String,
        address: String,
    ): Result<User> =
        try {
            val response =
                apiService.signup(
                    NewUserRequest(
                        email = email,
                        password = password,
                        phone = phone,
                        address = address,
                        name = name,
                    ),
                )
            Result.success(
                User(
                    id = response.id,
                    email = response.email,
                    name = response.name,
                    address = response.address,
                    phone = response.phone,
                ),
            )
        } catch (e: Exception) {
            Result.failure(e)
        }

    override fun isLoggedIn() = tokenStorage.isTokenValid()
}
