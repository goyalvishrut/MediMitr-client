package org.example.medimitr.domain.order

import org.example.medimitr.data.api.ApiService
import org.example.medimitr.domain.cart.CartItem

class OrderRepositoryImpl(
    private val apiService: ApiService,
) : OrderRepository {
    override suspend fun placeOrder(cartItems: List<CartItem>): Result<Order> =
        try {
            val total = cartItems.sumOf { it.medicine.price * it.quantity }
            val order =
                Order(
                    id = "",
                    status = "",
                    items = cartItems,
                    total = total,
                    datePlaced = "",
                )
            val response = apiService.placeOrder(order)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
