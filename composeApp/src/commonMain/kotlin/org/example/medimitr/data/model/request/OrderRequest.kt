package org.example.medimitr.data.model.request

import kotlinx.serialization.Serializable

@Serializable
data class OrderRequest(
    val userId: String, // Or obtained from auth context
    val items: List<OrderItemRequest>,
    val deliveryAddress: String,
    val totalAmount: Double,
    val phone: String,
    val prescriptionUrl: String?, // URL after upload
) {
    @Serializable
    data class OrderItemRequest(
        val medicineId: Int, // Medicine ID
        val quantity: Int, // Quantity ordered
        val price: Double, // Price per item
    )
}
