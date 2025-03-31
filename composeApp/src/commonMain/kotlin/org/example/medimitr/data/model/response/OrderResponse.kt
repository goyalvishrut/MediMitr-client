package org.example.medimitr.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    val orderId: String,
    val status: String,
    val estimatedDelivery: String?, // Or Date
)
