package org.example.medimitr.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class PromotionResponse(
    val id: String,
    val title: String,
    val imageUrl: String,
    val deeplink: String? = null, // For navigation on click
)
