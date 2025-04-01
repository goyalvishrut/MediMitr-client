package org.example.medimitr.domain.order

import kotlinx.coroutines.flow.Flow
import org.example.medimitr.domain.cart.CartItem

interface OrderRepository {
    suspend fun placeOrder(cartItems: List<CartItem>): Result<Order>

    fun getOrderHistory(): Flow<Result<List<Order>>>

    fun getOrderById(orderId: Int): Flow<Result<Order>>

    suspend fun placeOrderNew(
        items: List<CartItem>,
        deliveryAddress: String,
        phone: String,
        totalAmount: Double,
        paymentMethod: String,
    ): Result<Order>
}
