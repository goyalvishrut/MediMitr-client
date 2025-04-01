package org.example.medimitr.domain.order

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.example.medimitr.data.api.ApiService
import org.example.medimitr.data.model.request.OrderRequest
import org.example.medimitr.domain.cart.CartItem
import org.example.medimitr.domain.medicine.Medicine

class OrderRepositoryImpl(
    private val apiService: ApiService,
) : OrderRepository {
    override fun getOrderHistory(): Flow<Result<List<Order>>> =
        flow {
            try {
                val response = apiService.getOrderHistory()
                val orders =
                    response.map {
                        Order(
                            id = it.id,
                            status = it.status,
                            items =
                                it.items.map { orderItem ->
                                    CartItem(
                                        medicine =
                                            Medicine(
                                                id = orderItem.medicineId.toString(),
                                                name = orderItem.medicineName,
                                                price = orderItem.price,
                                                imageUrl = "",
                                                description = "",
                                                requiresPrescription = false,
                                            ),
                                        quantity = orderItem.quantity,
                                    )
                                },
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
                        items =
                            response.items.map { orderItem ->
                                CartItem(
                                    medicine =
                                        Medicine(
                                            id = orderItem.medicineId.toString(),
                                            name = orderItem.medicineName,
                                            price = orderItem.price,
                                            imageUrl = "",
                                            description = "",
                                            requiresPrescription = false,
                                        ),
                                    quantity = orderItem.quantity,
                                )
                            },
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

    override suspend fun placeOrderNew(
        items: List<CartItem>,
        deliveryAddress: String,
        phone: String,
        totalAmount: Double,
        paymentMethod: String,
    ): Result<Order> =
        try {
            val total = items.sumOf { it.medicine.price * it.quantity }
            val orderRequest =
                OrderRequest(
                    items =
                        items.map {
                            OrderRequest.OrderItemRequest(
                                medicineId = it.medicine.id.toInt(),
                                quantity = it.quantity,
                                price = it.medicine.price,
                                medicineName = it.medicine.name,
                            )
                        },
                    deliveryAddress = deliveryAddress,
                    phone = phone,
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
