package org.example.medimitr.domain.location

import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {
    fun getCurrentCity(): StateFlow<String>

    fun selectCity(city: String)

    fun getAvailableCity(): StateFlow<List<String>>
}
