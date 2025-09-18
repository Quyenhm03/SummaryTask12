package com.example.summarytask12.model

import com.example.summarytask12.model.product.Product

data class Order (
    val id: String,
    val customer: Customer,
    val items: MutableList<Product> = mutableListOf(),
    var orderDate: String? = null,
) {

    val totalAmount: Double
        get() {
            var sum = 0.0
            for (item in items) {
                sum += item.getPrice()
            }
            return sum - items.sumOf { it.calculateDiscount(it.getPrice()) }
        }

    fun addItem(product: Product) : Boolean {
        if (product.getStock() > 0) {
            items.add(product)
            product.updateStock(-1)
            return true
        }
        return false
    }

    override fun toString(): String {
        val dateStr = orderDate ?: "Pending shipment"
        return "Order #${id} for ${customer.name}: ${items.size} items, Date: $dateStr, Total: ${totalAmount} VND"
    }
}