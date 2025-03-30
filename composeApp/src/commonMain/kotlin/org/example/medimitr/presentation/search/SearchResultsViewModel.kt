package org.example.medimitr.presentation.search

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.medimitr.domain.Medicine
import org.example.medimitr.domain.MedicineRepository
import org.example.medimitr.presentation.base.BaseViewModel
import org.koin.core.component.inject

class SearchResultsViewModel : BaseViewModel() {
    private val medicineRepository: MedicineRepository by inject() // Inject Repository

    private val _uiState = MutableStateFlow(SearchResultsUiState())
    val uiState: StateFlow<SearchResultsUiState> = _uiState.asStateFlow()

    fun searchMedicines(query: String) {
        if (query.isBlank()) {
            _uiState.value =
                SearchResultsUiState(medicines = emptyList()) // Clear results if query is blank
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null, searchQuery = query) }

        viewModelScope.launch {
            // Launch coroutine in ViewModel scope
            medicineRepository
                .searchMedicines(query)
                .catch { exception ->
                    // Catch errors from the Flow itself
                    _uiState.update {
                        it.copy(isLoading = false, error = "Failed to search: ${exception.message}")
                    }
                }.collect { result ->
                    // Collect results from the Flow
                    result
                        .onSuccess { medicines ->
                            _uiState.update {
                                it.copy(isLoading = false, medicines = medicines)
                            }
                        }.onFailure { exception ->
                            _uiState.update {
                                it.copy(isLoading = false, error = "Error: ${exception.message}")
                            }
                        }
                }
        }
    }
}

// --- Example: SearchResultsViewModel ---

data class SearchResultsUiState(
    val isLoading: Boolean = false,
    val medicines: List<Medicine> = emptyList(),
    val error: String? = null,
    val searchQuery: String = "",
)
