package com.example.summarytask12.model

import com.example.summarytask12.model.product.Product

data class Order(
    val id: String,
    val customer: Customer,
    val items: MutableList<Product> = mutableListOf(),
    var orderDate: String? = null
) {

    val totalAmount: Double
        get() = items.sumOf { it.getPropertyPrice() } - items.sumOf { it.calculateDiscount(it.getPropertyPrice()) }

    fun addItem(product: Product): Boolean {
        return if (product.getPropertyStock() > 0) {
            items.add(product)
            product.updateStock(- 1)
            true
        } else false
    }

    override fun toString(): String {
        val dateStr = orderDate ?: "Pending shipment"
        return "Order #${id} for ${customer.name}: ${items.size} items, Date: $dateStr, Total: ${totalAmount} VND"
    }
}