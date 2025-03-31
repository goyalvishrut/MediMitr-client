package org.example.medimitr.data.api

import org.example.medimitr.domain.auth.AuthResponse
import org.example.medimitr.domain.medicine.Medicine
import org.example.medimitr.domain.order.Order

interface ApiService {
    suspend fun login(
        username: String,
        password: String,
    ): AuthResponse

    suspend fun signup(
        username: String,
        password: String,
        email: String,
    ): AuthResponse

    suspend fun searchMedicines(query: String): List<Medicine>

    suspend fun placeOrder(order: Order): Order
}
