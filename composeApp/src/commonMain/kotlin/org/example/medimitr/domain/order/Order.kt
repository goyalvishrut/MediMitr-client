package org.example.medimitr.domain.order

import org.example.medimitr.domain.cart.CartItem

data class Order(
    val id: Int,
    val status: String,
    val items: List<CartItem>,
    val total: Double,
    val datePlaced: String, // Consider using kotlinx-datetime later
)
