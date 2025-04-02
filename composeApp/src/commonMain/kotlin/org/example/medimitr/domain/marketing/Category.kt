package org.example.medimitr.domain.marketing

data class Category(
    val id: String,
    val name: String,
    val iconUrl: String? = null, // Optional icon
)
