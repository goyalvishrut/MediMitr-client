package org.example.medimitr.data

import kotlinx.serialization.Serializable
import org.example.medimitr.domain.Medicine

@Serializable
data class MedicineDto(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val requiresPrescription: Boolean,
    val imageUrl: String?,
) {
    fun toDomain(): Medicine =
        Medicine(
            id = this.id,
            name = this.name,
            description = this.description,
            price = this.price,
            requiresPrescription = this.requiresPrescription,
            imageUrl = this.imageUrl,
        )
}

@Serializable
data class OrderRequestDto(
    val userId: String, // Or obtained from auth context
    val items: List<OrderItemDto>,
    val deliveryAddress: AddressDto,
    val prescriptionUrl: String?, // URL after upload
    val paymentMethod: String, // Will be "COD" for MVP
)

@Serializable
data class OrderItemDto(
    val medicineId: String,
    val quantity: Int,
)

@Serializable
data class AddressDto(
    val street: String,
    val city: String,
    val state: String,
    val pincode: String,
    val phone: String,
)

@Serializable
data class OrderResponseDto(
    val orderId: String,
    val status: String,
    val estimatedDelivery: String?, // Or Date
)
// Add DTOs for LoginRequest, LoginResponse, UserProfile, etc.
