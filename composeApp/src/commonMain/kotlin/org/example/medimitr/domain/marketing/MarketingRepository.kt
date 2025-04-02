package org.example.medimitr.domain.marketing

import org.example.medimitr.domain.medicine.Medicine

interface MarketingRepository {
    suspend fun getPromotions(): List<Promotion>

    suspend fun getCategories(): List<Category>

    suspend fun getFeaturedMedicines(): List<Medicine>
}
