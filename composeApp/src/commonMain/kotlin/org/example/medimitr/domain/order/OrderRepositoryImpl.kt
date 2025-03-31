package org.example.medimitr.domain.order

import org.example.medimitr.data.api.ApiService
import org.example.medimitr.data.model.request.OrderRequest
import org.example.medimitr.domain.cart.CartItem

class OrderRepositoryImpl(
    private val apiService: ApiService,
) : OrderRepository {
    override suspend fun placeOrder(cartItems: List<CartItem>): Result<Order> =
        try {
            val total = cartItems.sumOf { it.medicine.price * it.quantity }
            val orderRequest =
                OrderRequest(
                    items =
                        cartItems.map {
                            OrderRequest.OrderItemRequest(
                                medicineId = it.medicine.id.toInt(),
                                quantity = it.quantity,
                                price = it.medicine.price,
                            )
                        },
                    deliveryAddress = "",
                    phone = "",
                    totalAmount = total,
                    prescriptionUrl = null,
                )
            val response = apiService.placeOrder(orderRequest)
            val result =
                Order(
                    id = response.id,
                    status = response.status,
                    items = emptyList(),
                    total = response.totalAmount,
                    datePlaced = response.orderDate,
                )
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
