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
        username: String,
        password: String,
    ): AuthResponse =
        client
            .post("$BASE_URL/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username, "password" to password))
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

    override suspend fun searchMedicines(query: String): List<MedicineDto> {
        // Error handling (try-catch) is crucial here in real code
        return client
            .get("$BASE_URL/medicines/search") {
                parameter("q", query)
            }.body() // Ktor automatically deserializes based on ContentNegotiation
    }

    override suspend fun getMedicineDetails(id: String): MedicineDto? {
        try {
            return client.get("$BASE_URL/medicines/$id").body()
        } catch (e: Exception) {
            // Handle specific exceptions (e.g., 404 Not Found)
            println("Error fetching medicine details: $e")
            return null
        }
    }

    companion object {
        private const val BASE_URL = ""
    }
}
