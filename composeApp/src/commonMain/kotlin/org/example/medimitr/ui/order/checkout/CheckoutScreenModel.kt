package org.example.medimitr.ui.order.checkout

import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.medimitr.domain.auth.UserRepository
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.domain.order.OrderRepository
import org.example.medimitr.presentation.base.BaseScreenModel
import org.example.medimitr.ui.order.cart.PriceDetails
import org.example.medimitr.ui.order.orderconfirmation.OrderPlacedScreen

data class CheckoutUiState(
    val isLoadingAddress: Boolean = true,
    val deliveryAddress: String = "", // Currently selected/displayed address
    val isEditingAddress: Boolean = false,
    val addressError: String? = null,
    val priceDetails: PriceDetails, // Passed in, holds breakdown and total
    val paymentMethod: String = "Cash on Delivery", // Static for MVP
    val isPlacingOrder: Boolean = false,
    val placeOrderError: String? = null,
)

class CheckoutScreenModel(
    private val cartRepository: CartRepository,
    private val userRepository: UserRepository,
    private val orderRepository: OrderRepository,
    passedPriceDetails: PriceDetails, // Receive PriceDetails during creation
) : BaseScreenModel() {
    // Use mutableStateOf for simpler single-value states if preferred over full StateFlow emission
    // Or keep using StateFlow as below
    private val _uiState = MutableStateFlow(CheckoutUiState(priceDetails = passedPriceDetails))
    val uiState = _uiState.asStateFlow()

    init {
        loadUserDefaultAddress()
    }

    private fun loadUserDefaultAddress() {
        _uiState.update { it.copy(isLoadingAddress = true, addressError = null) }
        viewModelScope.launch {
            val result = userRepository.getCurrentUser() // Assuming this returns Flow<User?>
            if (result.isSuccess) {
                val user = result.getOrNull()

                if (user == null) {
                    _uiState.update {
                        it.copy(
                            isLoadingAddress = false,
                            addressError = "User not found.",
                        )
                    }
                    return@launch
                } else {
                    _uiState.update {
                        it.copy(
                            isLoadingAddress = false,
                            deliveryAddress = user.address,
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoadingAddress = false,
                        addressError = "User not found.",
                    )
                }
            }
        }
    }

    fun startEditingAddress() {
        _uiState.update { it.copy(isEditingAddress = true) }
    }

    fun cancelEditingAddress() {
        _uiState.update { it.copy(isEditingAddress = false) }
    }

    // Updates the address displayed *for this order* in the UI state
    // Doesn't persist the change to the user profile in this simple version
    fun updateSelectedAddress(newAddress: String) {
        _uiState.update { it.copy(deliveryAddress = newAddress, isEditingAddress = false) }
    }

    // TODO (Optional): Implement saveAddressToProfile(newAddress) if needed,
    // calling userRepository.updateAddress(newAddress)

    fun clearPlaceOrderError() {
        _uiState.update { it.copy(placeOrderError = null) }
    }

    fun onPlaceOrder(navigator: Navigator) {
        val currentState = _uiState.value
        if (currentState.deliveryAddress.isBlank()) {
            _uiState.update { it.copy(placeOrderError = "Please provide a delivery address.") }
            return
        }
        if (currentState.isPlacingOrder) return // Prevent multiple clicks

        _uiState.update { it.copy(isPlacingOrder = true, placeOrderError = null) }

        viewModelScope.launch {
            // Fetch latest cart items directly for the order payload
            val currentCartItems = cartRepository.cartItems.firstOrNull() ?: emptyList()

            if (currentCartItems.isEmpty()) {
                _uiState.update {
                    it.copy(
                        isPlacingOrder = false,
                        placeOrderError = "Your cart is empty.",
                    )
                }
                return@launch
            }

            // Call repository to place order
            val result =
                orderRepository.placeOrderNew(
                    items = currentCartItems,
                    deliveryAddress = currentState.deliveryAddress,
                    phone =
                        userRepository
                            .getCurrentUser()
                            .getOrNull()
                            ?.phone
                            .orEmpty(),
                    // Get phone if needed
                    totalAmount = currentState.priceDetails.total,
                    paymentMethod = currentState.paymentMethod,
                )

            if (result.isSuccess) {
                cartRepository.clearCart() // Clear cart on success
                // Navigate to Order Placed screen - replace 'OrderPlacedScreen()' with your actual screen class
                navigator.replaceAll(
                    OrderPlacedScreen(
                        orderId = result.getOrNull()?.id.toString(),
                    ),
                ) // Pass order ID if returned
            } else {
                _uiState.update {
                    it.copy(
                        isPlacingOrder = false,
                        placeOrderError =
                            result.exceptionOrNull()?.message
                                ?: "Failed to place order. Please try again.",
                    )
                }
            }
        }
    }
}
