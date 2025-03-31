package org.example.medimitr.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderResponse(
    // Order entity
    val id: Int, // Order ID
    val userId: Int, // User who placed the order
    val orderDate: Long, // Date and time of order
    val status: String, // Order status
    val totalAmount: Double, // Total cost
    val deliveryAddress: String, // Delivery address
    val phone: String, // Contact phone
)
