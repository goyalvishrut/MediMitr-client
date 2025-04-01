package org.example.medimitr.domain.cart

import kotlinx.coroutines.flow.StateFlow
import org.example.medimitr.domain.medicine.Medicine

interface CartRepository {
    /**
     * Provides a reactive flow of the current list of items in the cart.
     */
    val cartItems: StateFlow<List<CartItem>>

    /**
     * Adds a medicine to the cart. If the medicine already exists, increments its quantity.
     * @param medicine The medicine to add.
     */
    fun addToCart(medicine: Medicine)

    /**
     * Completely removes a medicine item from the cart, regardless of quantity.
     * @param medicineId The ID of the medicine to remove.
     */
    fun removeFromCart(medicineId: String)

    /**
     * Updates the quantity of a specific item in the cart.
     * If the new quantity is 0 or less, the item is removed from the cart.
     * @param medicineId The ID of the medicine item to update.
     * @param newQuantity The desired new quantity.
     */
    fun updateItemQuantity(medicineId: String, newQuantity: Int) // Added function

    /**
     * Removes all items from the cart.
     */
    fun clearCart()
}
