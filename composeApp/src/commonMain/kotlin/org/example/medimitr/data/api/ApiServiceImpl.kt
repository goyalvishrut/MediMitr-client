package org.example.medimitr.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.medimitr.data.MedicineDto
import org.example.medimitr.domain.auth.AuthResponse
import org.example.medimitr.domain.order.Order

class ApiServiceImpl(
    private val client: HttpClient,
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

    override suspend fun placeOrder(order: Order): Order =
        client
            .post("$BASE_URL/orders") {
                contentType(ContentType.Application.Json)
                setBody(order)
            }.body()

    override suspend fun searchMedicines(query: String): List<MedicineDto> =
        client
            .get("$BASE_URL/medicines/search") {
                parameter("q", query)
            }.body()

    override suspend fun getMedicineDetails(id: String): MedicineDto? = client.get("$BASE_URL/medicines/$id").body()

    override suspend fun getAllMedicines(): List<MedicineDto> = client.get("$BASE_URL/medicines").body()

    companion object {
        private const val BASE_URL = "http://192.168.29.57:8080"
    }
}
