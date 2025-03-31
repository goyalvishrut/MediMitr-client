package org.example.medimitr.domain.order

import org.example.medimitr.domain.cart.CartItem

interface OrderRepository {
    suspend fun placeOrder(cartItems: List<CartItem>): Result<Order>
}
