package org.example.medimitr.domain.cart

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.example.medimitr.domain.medicine.Medicine

class CartRepositoryImpl : CartRepository {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> = _cartItems

    override fun addToCart(medicine: Medicine) {
        val current = _cartItems.value
        val existing = current.find { it.medicine.id == medicine.id }
        if (existing != null) {
            _cartItems.value =
                current.map {
                    if (it.medicine.id == medicine.id) it.copy(quantity = it.quantity + 1) else it
                }
        } else {
            _cartItems.value = current + CartItem(medicine, 1)
        }
    }

    override fun removeFromCart(medicineId: String) {
        _cartItems.value = _cartItems.value.filter { it.medicine.id != medicineId }
    }

    override fun clearCart() {
        _cartItems.value = emptyList()
    }
}
