package org.example.medimitr.domain.auth

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getCurrentUser(): Flow<Result<User>>

    fun updateEmail(newValue: String): Flow<Result<Unit>>

    fun updateAddress(newValue: String): Flow<Result<Unit>>

    fun updatePhone(newValue: String): Flow<Result<Unit>>

    fun changePassword(
        oldPass: String,
        newPass: String,
    ): Flow<Result<Unit>>

    fun logout()
}
