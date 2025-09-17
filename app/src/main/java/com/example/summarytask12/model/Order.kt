package com.example.summarytask12.model

import com.example.summarytask12.model.product.Product

data class Order (
    private val id: String,
    private val customer: Customer,
    private val items: MutableList<Product> = mutableListOf(),
    private var orderDate: String? = null,
) {
    fun getId() = id
    fun getCustomer() = customer
    fun getItems() = items
    fun getOrderDate() = orderDate

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

    fun setOrderDate(date: String) {
        orderDate = date
    }

    override fun toString(): String {
        val dateStr = orderDate ?: "Pending shipment"
        return "Order #${id} for ${customer.getName()}: ${items.size} items, Date: $dateStr, Total: ${totalAmount} VND"
    }
}