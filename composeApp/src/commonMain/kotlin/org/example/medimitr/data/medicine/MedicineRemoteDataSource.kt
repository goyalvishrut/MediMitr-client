package org.example.medimitr.data.medicine

import org.example.medimitr.domain.medicine.Medicine

interface MedicineRemoteDataSource {
    suspend fun searchMedicines(query: String): List<Medicine>

    suspend fun getMedicineDetails(id: String): Medicine?

    suspend fun getAllMedicines(): List<Medicine>
}
