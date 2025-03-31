package org.example.medimitr.data.medicine

import org.example.medimitr.data.MedicineDto
import org.example.medimitr.data.api.ApiService

class MedicineRemoteDataSourceImpl(
    private val apiService: ApiService,
) : MedicineRemoteDataSource {
    override suspend fun searchMedicines(query: String): List<MedicineDto> = apiService.searchMedicines(query)

    override suspend fun getMedicineDetails(id: String): MedicineDto? = apiService.getMedicineDetails(id)
}
