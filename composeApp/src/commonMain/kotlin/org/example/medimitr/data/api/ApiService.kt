package org.example.medimitr.data.api

import org.example.medimitr.data.model.request.OrderRequest
import org.example.medimitr.data.model.response.MedicineResponse
import org.example.medimitr.data.model.response.OrderResponse
import org.example.medimitr.domain.auth.AuthResponse

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

    suspend fun placeOrder(orderRequest: OrderRequest): OrderResponse

    suspend fun searchMedicines(query: String): List<MedicineResponse>

    suspend fun getMedicineDetails(id: String): MedicineResponse?

    suspend fun getAllMedicines(): List<MedicineResponse>
}
