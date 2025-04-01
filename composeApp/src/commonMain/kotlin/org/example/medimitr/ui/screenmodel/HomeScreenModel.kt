package org.example.medimitr.ui.screenmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.domain.medicine.Medicine
import org.example.medimitr.domain.medicine.MedicineRepository
import org.example.medimitr.presentation.base.BaseScreenModel

// ui/screenmodel/HomeScreenModel.kt
class HomeScreenModel(
    private val medicineRepository: MedicineRepository,
) : BaseScreenModel() {
    var searchQuery by mutableStateOf("")
    var medicines by mutableStateOf<List<Medicine>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun onSearch() {
        if (searchQuery.isBlank()) return
        isLoading = true
        viewModelScope.launch {
            val result =
                medicineRepository.getAllMedicines().collect {
                    isLoading = false
                    medicines = it.getOrElse { emptyList() }
                }
        }
    }

    fun onAddToCart(
        medicine: Medicine,
        cartRepository: CartRepository,
    ) {
        cartRepository.addToCart(medicine)
    }
}
