package org.example.medimitr.ui.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.medimitr.domain.cart.CartItem
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.domain.location.LocationRepository
import org.example.medimitr.domain.marketing.Category
import org.example.medimitr.domain.marketing.MarketingRepository
import org.example.medimitr.domain.marketing.Promotion
import org.example.medimitr.domain.medicine.Medicine
import org.example.medimitr.presentation.base.BaseViewModel

data class HomeUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedCity: String = "Loading...", // Start with loading state
    val availableCities: List<String> = emptyList(),
    val showCitySelection: Boolean = false,
    val promotions: List<Promotion> = emptyList(),
    val categories: List<Category> = emptyList(),
    val featuredItems: List<Medicine> = emptyList(),
    val cartItems: List<CartItem> = emptyList(), // Observe cart state
    val showRemoveConfirmation: Boolean = false,
    val itemPendingRemoval: Medicine? = null, // Store Medicine for confirmation dialog
)

// ui/screenmodel/HomeScreenModel.kt
class HomeScreenViewModel(
    private val cartRepository: CartRepository,
    private val locationRepository: LocationRepository,
    private val marketingRepository: MarketingRepository,
) : BaseViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Observe cart items continuously
        viewModelScope.launch {
            cartRepository.cartItems.collect { items ->
                _uiState.update { it.copy(cartItems = items) }
            }
        }
        // Observe selected city continuously
        viewModelScope.launch {
            locationRepository
                .getCurrentCity() // Assuming StateFlow<String>
                // Default on error
                .collect { city -> _uiState.update { it.copy(selectedCity = city) } }
        }
        viewModelScope.launch {
            locationRepository.getAvailableCity().collect { cities ->
                _uiState.update { it.copy(availableCities = cities) }
            }
        }

        // Load initial data
        loadHomeScreenData()
    }

    fun loadHomeScreenData() {
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            try {
                // Fetch in parallel if possible, or sequentially
                // Using combine might be better for parallel fetching and combining results
                val promotions = marketingRepository.getPromotions() // Assume suspend fun or Flow
                val categories = marketingRepository.getCategories()
                val featured = marketingRepository.getFeaturedMedicines()
                // val cities = locationRepository.getAvailableCities() // Fetch if not mock

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        promotions = promotions,
                        categories = categories,
                        featuredItems = featured,
                        // availableCities = cities // Update if fetched
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load data: ${e.message}",
                    )
                }
            }
        }
    }

    // --- City Selection ---
    fun toggleCitySelection(show: Boolean) {
        _uiState.update { it.copy(showCitySelection = show) }
    }

    fun selectCity(city: String) {
        _uiState.update { it.copy(showCitySelection = false) } // Hide sheet after selection
        viewModelScope.launch {
            locationRepository.selectCity(city) // Update preference
            _uiState.update { it.copy(selectedCity = city) } // Update UI state
            // Optionally reload data based on the new city
            loadHomeScreenData()
        }
    }

    // --- Cart Interaction ---
    fun getCartQuantity(medicineId: String): Int =
        _uiState.value.cartItems
            .find { it.medicine.id == medicineId }
            ?.quantity ?: 0

    fun onAddToCart(medicine: Medicine) {
        // Directly add, repo handles incrementing if exists
        cartRepository.addToCart(medicine)
    }

    fun onUpdateQuantity(
        medicine: Medicine,
        newQuantity: Int,
    ) {
        if (newQuantity <= 0) {
            // Show confirmation dialog before removing
            _uiState.update {
                it.copy(
                    showRemoveConfirmation = true,
                    itemPendingRemoval = medicine,
                )
            }
        } else {
            // Update quantity (handle potential repo errors if needed)
            viewModelScope.launch {
                try {
                    cartRepository.updateItemQuantity(medicine.id, newQuantity)
                } catch (e: Exception) {
                    // Handle error
                }
            }
        }
    }

    fun confirmRemoveItem() {
        val itemToRemove = _uiState.value.itemPendingRemoval
        if (itemToRemove != null) {
            viewModelScope.launch {
                try {
                    cartRepository.removeFromCart(itemToRemove.id)
                } catch (e: Exception) {
                    // Handle error
                }
                // Hide dialog after initiating action
                _uiState.update {
                    it.copy(
                        showRemoveConfirmation = false,
                        itemPendingRemoval = null,
                    )
                }
            }
        }
    }

    fun cancelRemoveItem() {
        _uiState.update { it.copy(showRemoveConfirmation = false, itemPendingRemoval = null) }
    }

    // --- Utility ---
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
