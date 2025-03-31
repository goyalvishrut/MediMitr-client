package org.example.medimitr.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.headers
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import org.example.medimitr.data.local.TokenManager
import org.example.medimitr.data.model.request.OrderRequest
import org.example.medimitr.data.model.response.MedicineResponse
import org.example.medimitr.domain.auth.AuthResponse
import org.example.medimitr.domain.order.Order

class ApiServiceImpl(
    private val client: HttpClient,
    private val tokenManager: TokenManager,
) : ApiService {
    override suspend fun login(
        email: String,
        password: String,
    ): AuthResponse =
        client
            .post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to email, "password" to password))
            }.body()

    override suspend fun signup(
        username: String,
        password: String,
        email: String,
    ): AuthResponse =
        client
            .post("$BASE_URL/signup") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username, "password" to password, "email" to email))
            }.body()

    override suspend fun placeOrder(orderRequest: OrderRequest): Order =
        client
            .post("$BASE_URL/orders") {
                contentType(ContentType.Application.Json)
                setBody(orderRequest)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "Bearer ${tokenManager.getToken()}",
                    )
                }
            }.body()

    override suspend fun searchMedicines(query: String): List<MedicineResponse> =
        client
            .get("$BASE_URL/medicines/search") {
                parameter("q", query)
            }.body()

    override suspend fun getMedicineDetails(id: String): MedicineResponse? = client.get("$BASE_URL/medicines/$id").body()

    override suspend fun getAllMedicines(): List<MedicineResponse> = client.get("$BASE_URL/medicines").body()

    companion object {
        private const val BASE_URL = "http://192.168.29.57:8080"
    }
}
