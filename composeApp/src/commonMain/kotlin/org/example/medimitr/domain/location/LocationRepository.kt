package org.example.medimitr.domain.location

import kotlinx.coroutines.flow.StateFlow
import org.example.medimitr.domain.medicine.Medicine
import org.example.medimitr.domain.promotion.Category
import org.example.medimitr.domain.promotion.Promotion

interface LocationRepository {
    fun getCurrentCity(): StateFlow<String>

    fun selectCity(city: String)

    fun getAvailableCity(): StateFlow<List<String>>

    fun getPromotions(): List<Promotion>

    fun getCategories(): List<Category>

    fun getFeaturedMedicines(): List<Medicine>
}
