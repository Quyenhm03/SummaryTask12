package com.example.summarytask12.model.order

import com.example.summarytask12.model.product.Product

data class OrderItem(
    val product: Product,
    var quantity: Int,
    val unitPrice: Double,
) {
    fun getSubtotal(): Double = quantity * unitPrice
    fun getDiscount(): Double = product.calculateDiscountProduct(getSubtotal())
}