package org.example.medimitr.domain.medicine

import kotlinx.coroutines.flow.Flow
import org.example.medimitr.domain.Medicine

interface MedicineRepository {
    // Use Flow for reactive data streams to UI
    fun searchMedicines(query: String): Flow<Result<List<Medicine>>>

    fun getMedicineDetails(id: String): Flow<Result<Medicine?>>
    // Add other methods
}
