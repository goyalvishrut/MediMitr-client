package org.example.medimitr.domain.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String,
)
