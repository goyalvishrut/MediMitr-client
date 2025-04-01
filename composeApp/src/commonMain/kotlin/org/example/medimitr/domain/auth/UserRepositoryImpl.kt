package org.example.medimitr.domain.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import org.example.medimitr.data.api.ApiService
import org.example.medimitr.data.local.TokenStorage

class UserRepositoryImpl(
    private val apiService: ApiService,
    private val tokenStorage: TokenStorage,
) : UserRepository {
    private var authStatus = AuthStatus.UNKNOWN

    override val observeAuthStatus: MutableStateFlow<AuthStatus> = MutableStateFlow(authStatus)

    override suspend fun getCurrentUser(): Flow<Result<User>> =
        flow {
            try {
                val token = tokenStorage.getToken()
                if (token == null) {
                    emit(Result.failure(Exception("No token found")))
                    return@flow
                }
                val response = apiService.getUser(token)
                val user =
                    User(
                        id = response.id,
                        email = response.email,
                        address = response.address,
                        phone = response.phone,
                        name = response.name,
                    )
                emit(Result.success(user))
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }

    override fun updateEmail(newValue: String): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override fun updateAddress(newValue: String): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override fun updatePhone(newValue: String): Flow<Result<Unit>> {
        TODO("Not yet implemented")
    }

    override fun changePassword(
        oldPass: String,
        newPass: String,
    ): Flow<Result<Unit>> {
        TODO("Not yet implemented")
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
