package org.example.medimitr.ui.screenmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.navigator.Navigator
import org.example.medimitr.domain.cart.CartRepository
import org.example.medimitr.ui.screens.order.CheckoutScreen

// ui/screenmodel/CartScreenModel.kt
class CartScreenModel(
    private val cartRepository: CartRepository,
) : ScreenModel {
    val cartItems = cartRepository.cartItems

    fun onRemoveFromCart(medicineId: String) {
        cartRepository.removeFromCart(medicineId)
    }

    fun onProceedToCheckout(navigator: Navigator) {
        if (cartItems.value.isNotEmpty()) {
            navigator.push(CheckoutScreen())
        }
    }
}
