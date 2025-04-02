package org.example.medimitr.domain.location

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LocationRepositoryImpl : LocationRepository {
    private var selectedCity: String = "Raigarh"

    override fun getCurrentCity(): StateFlow<String> = MutableStateFlow(selectedCity).asStateFlow()

    override fun selectCity(city: String) {
        selectedCity = city
    }

    override fun getAvailableCity(): StateFlow<List<String>> = MutableStateFlow(listOf("Raigarh", "Raipur", "Bilaspur")).asStateFlow()
}
