package org.example.medimitr.data.api

import org.example.medimitr.data.MedicineDto
import org.example.medimitr.domain.auth.AuthResponse
import org.example.medimitr.domain.order.Order

interface ApiService {
    suspend fun login(
        email: String,
        password: String,
    ): AuthResponse

    suspend fun signup(
        username: String,
        password: String,
        email: String,
    ): AuthResponse

    suspend fun placeOrder(order: Order): Order

    suspend fun searchMedicines(query: String): List<MedicineDto>

    suspend fun getMedicineDetails(id: String): MedicineDto?

    suspend fun getAllMedicines(): List<MedicineDto>
}
