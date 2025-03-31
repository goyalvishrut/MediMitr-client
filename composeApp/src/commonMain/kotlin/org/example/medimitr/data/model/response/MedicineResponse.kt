package org.example.medimitr.data.model.response

import kotlinx.serialization.Serializable
import org.example.medimitr.domain.medicine.Medicine

@Serializable
data class MedicineResponse(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val requiresPrescription: Boolean = false,
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
