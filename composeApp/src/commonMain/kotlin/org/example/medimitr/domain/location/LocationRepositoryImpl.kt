package org.example.medimitr.domain.location

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.medimitr.domain.medicine.Medicine
import org.example.medimitr.domain.promotion.Category
import org.example.medimitr.domain.promotion.Promotion

class LocationRepositoryImpl : LocationRepository {
    override fun getCurrentCity(): StateFlow<String> = MutableStateFlow("Raigarh").asStateFlow()

    override fun selectCity(city: String) {
    }

    override fun getAvailableCity(): StateFlow<List<String>> = MutableStateFlow(listOf("Raigarh", "Raipur", "Bilaspur")).asStateFlow()

    override fun getPromotions(): List<Promotion> = emptyList()

    override fun getCategories(): List<Category> = emptyList()

    override fun getFeaturedMedicines(): List<Medicine> = emptyList()
}
