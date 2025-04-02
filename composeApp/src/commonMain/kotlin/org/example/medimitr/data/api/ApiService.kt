package org.example.medimitr.data.api

import org.example.medimitr.data.model.request.NewUserRequest
import org.example.medimitr.data.model.request.OrderRequest
import org.example.medimitr.data.model.response.AuthResponse
import org.example.medimitr.data.model.response.MedicineResponse
import org.example.medimitr.data.model.response.OrderHistoryResponse
import org.example.medimitr.data.model.response.OrderResponse
import org.example.medimitr.data.model.response.UserDetailsResponse

interface ApiService {
    suspend fun login(
        email: String,
        password: String,
    ): AuthResponse

    suspend fun signup(newUserRequest: NewUserRequest): UserDetailsResponse

    suspend fun placeOrder(orderRequest: OrderRequest): OrderResponse

    suspend fun searchMedicines(query: String): List<MedicineResponse>

    suspend fun getMedicineDetails(id: String): MedicineResponse?

    suspend fun getAllMedicines(): List<MedicineResponse>

    suspend fun getOrderHistory(): List<OrderHistoryResponse>

    suspend fun getOrderById(orderId: Int): OrderHistoryResponse

    suspend fun getUser(token: String): UserDetailsResponse

    suspend fun updateEmail(newEmail: String): Boolean

    suspend fun updateAddress(newValue: String): Boolean

    suspend fun updatePhone(newValue: String): Boolean

    suspend fun updatePassword(
        oldPass: String,
        newPass: String,
    ): Boolean
}
