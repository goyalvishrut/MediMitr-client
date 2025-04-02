package org.example.medimitr.domain.promotion

data class Promotion(
    val id: String,
    val title: String,
    val imageUrl: String,
    val deeplink: String? = null, // For navigation on click
)
