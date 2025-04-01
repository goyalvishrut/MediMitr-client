package org.example.medimitr.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class UserDetailsResponse(
    // User entity
    val id: Int, // User ID
    val email: String, // User email
    val name: String, // User name
    val address: String, // User address
    val phone: String, // User phone number
)
