package org.example.medimitr.domain.auth

data class User(
    val id: Int, // User ID
    val email: String, // User email
    val name: String, // User name
    val address: String, // User address
    val phone: String, // User phone number
)
