package org.example.medimitr.domain.auth

import kotlinx.coroutines.flow.StateFlow

interface UserRepository {
    val observeAuthStatus: StateFlow<AuthStatus>

    suspend fun getCurrentUser(): Result<User>

    suspend fun updateEmail(newEmail: String): Result<Boolean>

    suspend fun updateAddress(newValue: String): Result<Boolean>

    suspend fun updatePhone(newValue: String): Result<Boolean>

    suspend fun changePassword(
        oldPass: String,
        newPass: String,
    ): Result<Boolean>

    fun logout()

    fun updateAuthStatus()
}
