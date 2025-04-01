package org.example.medimitr.domain.cart

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import org.example.medimitr.domain.medicine.Medicine

// Simple in-memory implementation for the Cart Repository
class CartRepositoryImpl : CartRepository {
    // Use MutableStateFlow to hold the cart state in memory.
    // In a real app, consider persisting this (DataStore, SQLDelight, Server).
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    override val cartItems: StateFlow<List<CartItem>> = _cartItems

    override fun addToCart(medicine: Medicine) {
        _cartItems.update { currentCart ->
            // Use update for safer concurrent modification
            val existingItem = currentCart.find { it.medicine.id == medicine.id }
            if (existingItem != null) {
                // Map to a new list with the updated quantity
                currentCart.map {
                    if (it.medicine.id == medicine.id) {
                        it.copy(quantity = it.quantity + 1)
                    } else {
                        it
                    }
                }
            } else {
                // Add the new item to the list
                currentCart + CartItem(medicine = medicine, quantity = 1)
            }
        }
    }

    override fun removeFromCart(medicineId: String) {
        _cartItems.update { currentCart ->
            // Filter out the item with the matching medicineId
            currentCart.filter { it.medicine.id != medicineId }
        }
    }

    /**
     * Updates the quantity or removes the item if quantity <= 0.
     */
    override fun updateItemQuantity(
        medicineId: String,
        newQuantity: Int,
    ) {
        if (newQuantity <= 0) {
            // If new quantity is zero or less, remove the item completely
            removeFromCart(medicineId)
        } else {
            _cartItems.update { currentCart ->
                currentCart.map {
                    if (it.medicine.id == medicineId) {
                        // Update the quantity for the matching item
                        it.copy(quantity = newQuantity)
                    } else {
                        it // Keep other items as they are
                    }
                }
                // Note: This map operation assumes the item already exists.
                // If it might not, add a check: currentCart.any { it.medicine.id == medicineId }
            }
        }
    }

    override fun clearCart() {
        _cartItems.value = emptyList() // Simple reset
    }
}
