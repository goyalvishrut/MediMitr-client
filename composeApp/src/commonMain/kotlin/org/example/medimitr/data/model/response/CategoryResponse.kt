package org.example.medimitr.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class CategoryResponse(
    val id: String,
    val name: String,
    val iconUrl: String? = null, // Optional icon
)
