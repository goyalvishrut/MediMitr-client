package org.example.medimitr.domain.promotion

data class Category(
    val id: String,
    val name: String,
    val iconUrl: String? = null, // Optional icon
)
