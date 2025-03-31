package org.example.medimitr.data.base

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

open class BaseApi(
    val client: HttpClient,
) {
    init {
        client.config {
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    },
                )
            }
        }
    }

    protected suspend inline fun <reified T> get(
        path: String,
        configure: HttpRequestBuilder.() -> Unit = {},
    ): T = client.get(path, configure).body()

    protected suspend inline fun <reified T> post(
        path: String,
        body: Any,
        configure: HttpRequestBuilder.() -> Unit = {},
    ): T =
        client
            .post(path) {
                configure()
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body()

    protected suspend inline fun <reified T> put(
        path: String,
        body: Any,
        configure: HttpRequestBuilder.() -> Unit = {},
    ): T =
        client
            .put(path) {
                configure()
                contentType(ContentType.Application.Json)
                setBody(body)
            }.body()

    protected suspend inline fun <reified T> delete(
        path: String,
        configure: HttpRequestBuilder.() -> Unit = {},
    ): T = client.delete(path, configure).body()

    protected fun URLBuilder.path(vararg paths: String) {
        paths.forEach { path ->
            encodedPath += "/$path"
        }
    }
}
