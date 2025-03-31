package org.example.medimitr.network

import io.ktor.client.*
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

// Expect function to get the platform-specific engine
internal expect fun httpClientEngine(): HttpClientEngine

// Function to create the HttpClient (used by Koin)
fun createHttpClient(): HttpClient =
    HttpClient(CIO) {
        // Logging
        install(Logging) {
            level = LogLevel.INFO // Or LogLevel.ALL for more detail
            logger = Logger.DEFAULT
        }

        // JSON Serialization
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true // Important for API evolution
                },
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 5000 // 10 seconds
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 5000
        }

        // Default request configuration (e.g., Base URL - if applicable)
        // install(DefaultRequest) {
        //    url("YOUR_BASE_API_URL") // Set your actual base URL here
        //    header(HttpHeaders.ContentType, ContentType.Application.Json)
        // }

        // Add other plugins like Auth for handling tokens if needed later
    }
