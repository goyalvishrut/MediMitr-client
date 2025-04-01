package org.example.medimitr.ui.screenmodel

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.medimitr.domain.cart.CartItem
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.presentation.base.BaseScreenModel
import org.example.medimitr.ui.screens.order.CheckoutScreen

data class PriceDetails(
    val subtotal: Double = 0.0,
    val deliveryCharge: Double = 40.0, // Dummy value
    val discount: Double = 15.0, // Dummy value
    val platformFee: Double = 5.0, // Dummy value
    val total: Double = 0.0,
)

data class CartUiState(
    val isLoading: Boolean = false,
    val cartItems: List<CartItem> = emptyList(),
    val error: String? = null,
    val priceDetails: PriceDetails = PriceDetails(),
    val showPriceDetailsSheet: Boolean = false,
    val showRemoveConfirmationDialog: Boolean = false,
    val itemPendingRemoval: CartItem? = null, // Item to confirm removal for
)

// ui/screenmodel/CartScreenModel.kt
class CartScreenModel(
    private val cartRepository: CartRepository,
    // Inject other services if needed for price calculation
) : BaseScreenModel() {
    private val _uiState = MutableStateFlow(CartUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            cartRepository.cartItems // Observe items from repository
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Error loading cart: ${e.message}",
                        )
                    }
                }.collect { items ->
                    val calculatedDetails = calculatePriceDetails(items)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            cartItems = items,
                            priceDetails = calculatedDetails,
                        )
                    }
                }
        }
    }

    // --- Quantity and Removal ---

    fun updateQuantity(
        medicineId: String,
        quantity: Int,
    ) {
        if (quantity <= 0) {
            // If quantity becomes 0 or less, trigger removal confirmation logic
            val item = _uiState.value.cartItems.find { it.medicine.id == medicineId }
            if (item != null) {
                _uiState.update {
                    it.copy(showRemoveConfirmationDialog = true, itemPendingRemoval = item)
                }
            }
        } else {
            // Update quantity via repository (handle potential errors if needed)
            viewModelScope.launch {
                try {
                    cartRepository.updateItemQuantity(medicineId, quantity)
                    // State will update automatically via the collection flow
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Failed to update quantity: ${e.message}") }
                    // Optionally revert UI state or show specific feedback
                }
            }
        }
    }

    fun confirmRemoveItem() {
        val itemToRemove = _uiState.value.itemPendingRemoval
        if (itemToRemove != null) {
            viewModelScope.launch {
                try {
                    cartRepository.removeFromCart(itemToRemove.medicine.id)
                    // Hide dialog after action is initiated (state will update via flow)
                    _uiState.update {
                        it.copy(
                            showRemoveConfirmationDialog = false,
                            itemPendingRemoval = null,
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            error = "Failed to remove item: ${e.message}",
                            showRemoveConfirmationDialog = false,
                            itemPendingRemoval = null,
                        )
                    }
                }
            }
        }
    }

    fun cancelRemoveItem() {
        _uiState.update { it.copy(showRemoveConfirmationDialog = false, itemPendingRemoval = null) }
    }

    // --- Price Details Bottom Sheet ---

    private fun calculatePriceDetails(items: List<CartItem>): PriceDetails {
        val subtotal = items.sumOf { it.medicine.price * it.quantity }
        // Replace dummy values with actual calculation/fetching logic
        val deliveryCharge = if (items.isEmpty()) 0.0 else 40.0
        val discount = if (subtotal > 200) 15.0 else 0.0 // Example discount logic
        val platformFee = if (items.isEmpty()) 0.0 else 5.0
        val total = subtotal + deliveryCharge + platformFee - discount
        return PriceDetails(
            subtotal = subtotal,
            deliveryCharge = deliveryCharge,
            discount = discount,
            platformFee = platformFee,
            total = total.coerceAtLeast(0.0), // Ensure total isn't negative
        )
    }

    fun showPriceDetailsSheet() {
        _uiState.update { it.copy(showPriceDetailsSheet = true) }
    }

    fun hidePriceDetailsSheet() {
        _uiState.update { it.copy(showPriceDetailsSheet = false) }
    }

    // --- Navigation ---

    fun onProceedToCheckout(navigator: Navigator) {
        // Can add more checks here if needed (e.g., minimum order value)
        if (_uiState.value.cartItems.isNotEmpty()) {
            navigator.push(CheckoutScreen(priceDetails = uiState.value.priceDetails)) // Navigate to your checkout screen
        }
    }

    // --- Utility to clear errors ---
    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
