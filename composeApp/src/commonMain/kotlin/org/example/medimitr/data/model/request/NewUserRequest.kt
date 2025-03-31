package org.example.medimitr.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class NewUserRequest(
    val email: String, // Email
    val password: String, // Plaintext password
    val name: String, // Name
    val address: String, // Address
    val phone: String, // Phone number
)
