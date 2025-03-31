package org.example.medimitr.domain.cart

import kotlinx.coroutines.flow.StateFlow
import org.example.medimitr.domain.medicine.Medicine

interface CartRepository {
    val cartItems: StateFlow<List<CartItem>>

    fun addToCart(medicine: Medicine)

    fun removeFromCart(medicineId: String)

    fun clearCart()
}
