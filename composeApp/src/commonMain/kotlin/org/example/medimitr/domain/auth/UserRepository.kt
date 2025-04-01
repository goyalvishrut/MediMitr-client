package org.example.medimitr.domain.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val observeAuthStatus: StateFlow<AuthStatus>

    suspend fun getCurrentUser(): Result<User>

    fun updateEmail(newValue: String): Flow<Result<Unit>>

    fun updateAddress(newValue: String): Flow<Result<Unit>>

    fun updatePhone(newValue: String): Flow<Result<Unit>>

    fun changePassword(
        oldPass: String,
        newPass: String,
    ): Flow<Result<Unit>>

    fun logout()

    fun updateAuthStatus()
}
