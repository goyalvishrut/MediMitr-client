package org.example.medimitr.domain.cart

import org.example.medimitr.domain.medicine.Medicine

data class CartItem(
    val medicine: Medicine,
    val quantity: Int,
)
