package org.example.medimitr.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.medimitr.domain.auth.AuthResponse
import org.example.medimitr.domain.medicine.Medicine
import org.example.medimitr.domain.order.Order

class ApiServiceImpl(
    private val client: HttpClient,
) : ApiService {
    override suspend fun login(
        username: String,
        password: String,
    ): AuthResponse =
        client
            .post("/login") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username, "password" to password))
            }.body()

    override suspend fun signup(
        username: String,
        password: String,
        email: String,
    ): AuthResponse =
        client
            .post("/signup") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("username" to username, "password" to password, "email" to email))
            }.body()

    override suspend fun searchMedicines(query: String): List<Medicine> =
        client
            .get("/medicines") {
                parameter("query", query)
            }.body()

    override suspend fun placeOrder(order: Order): Order =
        client
            .post("/orders") {
                contentType(ContentType.Application.Json)
                setBody(order)
            }.body()
}
