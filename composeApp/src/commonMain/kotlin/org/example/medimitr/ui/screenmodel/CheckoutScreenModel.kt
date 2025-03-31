package org.example.medimitr.ui.screenmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.launch
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.domain.order.OrderRepository
import org.example.medimitr.presentation.base.BaseScreenModel
import org.example.medimitr.ui.screens.OrderPlacedScreen

// ui/screenmodel/CheckoutScreenModel.kt
class CheckoutScreenModel(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
) : BaseScreenModel() {
    val cartItems = cartRepository.cartItems
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun onPlaceOrder(navigator: Navigator) {
        if (cartItems.value.isEmpty()) {
            errorMessage = "Cart is empty"
            return
        }
        isLoading = true
        viewModelScope.launch {
            val result = orderRepository.placeOrder(cartItems.value)
            isLoading = false
            if (result.isSuccess) {
                cartRepository.clearCart()
                navigator.push(OrderPlacedScreen())
            } else {
                errorMessage = result.exceptionOrNull()?.message ?: "Order failed"
            }
        }
    }
}
