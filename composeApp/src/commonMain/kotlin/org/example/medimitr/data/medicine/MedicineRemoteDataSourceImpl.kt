package org.example.medimitr.data.medicine

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import org.example.medimitr.data.MedicineDto

class MedicineRemoteDataSourceImpl(
    private val httpClient: HttpClient,
    private val baseUrl: String = "YOUR_BASE_API_URL", // Inject or define base URL
) : MedicineRemoteDataSource {
    override suspend fun searchMedicines(query: String): List<MedicineDto> {
        // Error handling (try-catch) is crucial here in real code
        return httpClient
            .get("$baseUrl/medicines/search") {
                parameter("q", query)
            }.body() // Ktor automatically deserializes based on ContentNegotiation
    }

    override suspend fun getMedicineDetails(id: String): MedicineDto? {
        try {
            return httpClient.get("$baseUrl/medicines/$id").body()
        } catch (e: Exception) {
            // Handle specific exceptions (e.g., 404 Not Found)
            println("Error fetching medicine details: $e")
            return null
        }
    }
}
