package org.example.medimitr.domain.medicine

data class Medicine(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val requiresPrescription: Boolean,
    val imageUrl: String?,
)
