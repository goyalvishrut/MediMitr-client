package org.example.medimitr.data.medicine

import org.example.medimitr.data.MedicineDto

interface MedicineRemoteDataSource {
    suspend fun searchMedicines(query: String): List<MedicineDto>

    suspend fun getMedicineDetails(id: String): MedicineDto?
    // Add other methods like getCategories, getPromotions etc.
}
