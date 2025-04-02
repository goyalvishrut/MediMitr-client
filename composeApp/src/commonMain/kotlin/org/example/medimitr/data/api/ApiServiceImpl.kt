package org.example.medimitr.data.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.auth.AuthScheme
import io.ktor.http.contentType
import org.example.medimitr.data.local.TokenStorage
import org.example.medimitr.data.model.request.NewUserRequest
import org.example.medimitr.data.model.request.OrderRequest
import org.example.medimitr.data.model.response.AuthResponse
import org.example.medimitr.data.model.response.MedicineResponse
import org.example.medimitr.data.model.response.OrderHistoryResponse
import org.example.medimitr.data.model.response.OrderResponse
import org.example.medimitr.data.model.response.UserDetailsResponse

class ApiServiceImpl(
    private val client: HttpClient,
    private val tokenStorage: TokenStorage,
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

    override suspend fun signup(newUserRequest: NewUserRequest): UserDetailsResponse =
        client
            .post("$BASE_URL/create/user") {
                contentType(ContentType.Application.Json)
                setBody(newUserRequest)
            }.body()

    override suspend fun placeOrder(orderRequest: OrderRequest): OrderResponse =
        client
            .post("$BASE_URL/orders") {
                contentType(ContentType.Application.Json)
                setBody(orderRequest)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body()

    override suspend fun searchMedicines(query: String): List<MedicineResponse> =
        client
            .get("$BASE_URL/medicines/search") {
                parameter("q", query)
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body()

    override suspend fun getMedicineDetails(id: String): MedicineResponse? =
        client
            .get("$BASE_URL/medicines/$id") {
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body()

    override suspend fun getAllMedicines(): List<MedicineResponse> =
        client
            .get("$BASE_URL/medicines") {
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body()

    override suspend fun getOrderHistory(): List<OrderHistoryResponse> =
        client
            .get("$BASE_URL/orders") {
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body()

    override suspend fun getOrderById(orderId: Int): OrderHistoryResponse =
        client
            .get("$BASE_URL/orders/$orderId") {
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body()

    override suspend fun getUser(token: String): UserDetailsResponse {
        client
            .get("$BASE_URL/user") {
                contentType(ContentType.Application.Json)
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} $token",
                    )
                }
            }.body<UserDetailsResponse>()
            .let { user ->
                return user
            }
    }

    override suspend fun updateEmail(newEmail: String): Boolean {
        client
            .post("$BASE_URL/user/email") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("email" to newEmail))
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body<Boolean>()
            .let { response ->
                return response
            }
    }

    override suspend fun updateAddress(newValue: String): Boolean {
        client
            .post("$BASE_URL/user/address") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("address" to newValue))
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body<Boolean>()
            .let { response ->
                return response
            }
    }

    override suspend fun updatePhone(newValue: String): Boolean {
        client
            .post("$BASE_URL/user/phone") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("phone" to newValue))
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body<Boolean>()
            .let { response ->
                return response
            }
    }

    override suspend fun updatePassword(
        oldPass: String,
        newPass: String,
    ): Boolean {
        client
            .post("$BASE_URL/user/password") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("oldPassword" to oldPass, "newPassword" to newPass))
                headers {
                    append(
                        HttpHeaders.Authorization,
                        "${AuthScheme.Bearer} ${tokenStorage.getToken()}",
                    )
                }
            }.body<Boolean>()
            .let { response ->
                return response
            }
    }

    companion object {
        private const val BASE_URL = "http://192.168.29.57:8080"
    }
}
