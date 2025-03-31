package org.example.medimitr.data.model.response

import kotlinx.serialization.Serializable

@Serializable
data class AddressDto(
    val street: String,
    val city: String,
    val state: String,
    val pincode: String,
    val phone: String,
)
