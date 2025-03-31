package org.example.medimitr.data.medicine

import org.example.medimitr.data.api.ApiService
import org.example.medimitr.domain.medicine.Medicine

class MedicineRemoteDataSourceImpl(
    private val apiService: ApiService,
) : MedicineRemoteDataSource {
    override suspend fun searchMedicines(query: String): List<Medicine> = apiService.searchMedicines(query).map { it.toDomain() }

    override suspend fun getMedicineDetails(id: String): Medicine? = apiService.getMedicineDetails(id)?.toDomain()

    override suspend fun getAllMedicines(): List<Medicine> = apiService.getAllMedicines().map { it.toDomain() }
}
