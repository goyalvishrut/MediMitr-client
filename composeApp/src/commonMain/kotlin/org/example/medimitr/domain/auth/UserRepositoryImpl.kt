package org.example.medimitr.domain.auth

import kotlinx.coroutines.flow.MutableStateFlow
import org.example.medimitr.data.api.ApiService
import org.example.medimitr.data.local.TokenStorage

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val tokenStorage: TokenStorage,
) : UserRepository {
    private var authStatus = AuthStatus.UNKNOWN

    override val observeAuthStatus: MutableStateFlow<AuthStatus> = MutableStateFlow(authStatus)

    override suspend fun getCurrentUser(): Result<User> {
        try {
            val token =
                tokenStorage.getToken() ?: return Result.failure(Exception("Token not found"))
            val response = apiService.getUser(token)
            val user =
                User(
                    id = response.id,
                    email = response.email,
                    address = response.address,
                    phone = response.phone,
                    name = response.name,
                )
            return (Result.success(user))
        } catch (e: Exception) {
            return (Result.failure(e))
        }
    }

    override suspend fun updateEmail(newEmail: String): Result<Boolean> {
        try {
            tokenStorage.getToken() ?: return Result.failure(Exception("Token not found"))
            val response = apiService.updateEmail(newEmail)
            return Result.success(response)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun updateAddress(newValue: String): Result<Boolean> {
        try {
            tokenStorage.getToken() ?: return Result.failure(Exception("Token not found"))
            val response = apiService.updateAddress(newValue)
            return Result.success(response)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun updatePhone(newValue: String): Result<Boolean> {
        try {
            tokenStorage.getToken() ?: return Result.failure(Exception("Token not found"))
            val response = apiService.updatePhone(newValue)
            return Result.success(response)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override suspend fun changePassword(
        oldPass: String,
        newPass: String,
    ): Result<Boolean> {
        try {
            tokenStorage.getToken() ?: return Result.failure(Exception("Token not found"))
            val response = apiService.updatePassword(oldPass, newPass)
            return Result.success(response)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    override fun logout() {
        tokenStorage.clearToken()
        updateAuthStatus()
    }

    override fun updateAuthStatus() {
        authStatus =
            if (tokenStorage.isTokenValid()) {
                AuthStatus.LOGGED_IN
            } else {
                AuthStatus.LOGGED_OUT
            }
        observeAuthStatus.tryEmit(authStatus)
    }
}
