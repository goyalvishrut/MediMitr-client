package org.example.medimitr.domain

data class Medicine(
    val id: String,
    val name: String,
    val description: String?,
    val price: Double,
    val requiresPrescription: Boolean,
    val imageUrl: String?,
)

// Add models for User, CartItem, Order, Address etc.
// Example:
data class Order(
    val id: String,
    val status: String,
    val datePlaced: String, // Consider using kotlinx-datetime later
    val totalAmount: Double,
)
