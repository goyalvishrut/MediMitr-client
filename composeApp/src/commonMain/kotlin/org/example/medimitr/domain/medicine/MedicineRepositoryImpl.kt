package org.example.medimitr.domain.medicine

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.example.medimitr.data.medicine.MedicineRemoteDataSource

// Result wrapper for cleaner error handling
// (You can create a more sophisticated sealed class for Result)
typealias SimpleResult<T> = Result<T>

class MedicineRepositoryImpl(
    private val remoteDataSource: MedicineRemoteDataSource,
    // Inject localDataSource here later for caching
) : MedicineRepository {
    override fun searchMedicines(query: String): Flow<SimpleResult<List<Medicine>>> =
        flow {
            try {
                val dtoList = remoteDataSource.searchMedicines(query)
                val domainList = dtoList.map { it.toDomain() }
                emit(Result.success(domainList))
            } catch (e: Exception) {
                // Log the error
                println("Error in searchMedicines repo: $e")
                emit(Result.failure(e)) // Propagate error
            }
        }

    override fun getMedicineDetails(id: String): Flow<SimpleResult<Medicine?>> =
        flow {
            try {
                val dto = remoteDataSource.getMedicineDetails(id)
                emit(Result.success(dto?.toDomain()))
            } catch (e: Exception) {
                println("Error in getMedicineDetails repo: $e")
                emit(Result.failure(e))
            }
        }
}
