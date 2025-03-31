package org.example.medimitr.domain.order

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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

    override fun getOrderHistory(): Flow<Result<List<Order>>> =
        flow {
            try {
                val response = apiService.getOrderHistory()
                val orders =
                    response.map {
                        Order(
                            id = it.id,
                            status = it.status,
                            items = emptyList(),
                            total = it.totalAmount,
                            datePlaced = it.orderDate,
                        )
                    }
                emit(Result.success(orders))
            } catch (e: Exception) {
                // Log the error
                println("Error in getOrderHistory repo: $e")
                emit(Result.failure(e)) // Propagate error
            }
        }

    override fun getOrderById(orderId: Int): Flow<Result<Order>> =
        flow {
            try {
                val response = apiService.getOrderById(orderId)
                val orders =
                    Order(
                        id = response.id,
                        status = response.status,
                        items = emptyList(),
                        total = response.totalAmount,
                        datePlaced = response.orderDate,
                    )

                emit(Result.success(orders))
            } catch (e: Exception) {
                // Log the error
                println("Error in getOrderById repo: $e")
                emit(Result.failure(e)) // Propagate error
            }
        }
}
