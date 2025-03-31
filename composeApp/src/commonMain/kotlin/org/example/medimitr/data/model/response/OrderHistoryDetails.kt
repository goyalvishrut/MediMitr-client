package org.example.medimitr.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class OrderHistoryDetails(
    val medicineId: Int,
    val medicineName: String, // Assumed available for display
    val quantity: Int,
    val price: Double, // Price per item at time of order
)
