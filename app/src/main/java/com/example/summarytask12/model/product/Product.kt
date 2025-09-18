package com.example.summarytask12.model.product

import com.example.summarytask12.util.DatabaseConnect

abstract class Product (
    protected val id: String,
    protected val name: String,
    protected var price: Double,
    protected var stock: Int = 0
) : Discount {
    private var description: String?= null

    constructor(id: String, name: String, price: Double) : this(id, name, price, 0)
    constructor(id: String, name: String) : this(id, name, 0.0, 0)

    fun getId() = id
    fun getName() = name
    fun getPrice() = price
    fun getStock() = stock
    fun setDescription(newDescription: String?) {
        description = newDescription
    }

    fun updatePrice(newPrice: Double) {
        if (newPrice > 0 && newPrice != price) {
            price = newPrice
        }
    }

    fun updateStock(newStock: Int) {
        if (stock + newStock >= 0) {
            stock += newStock
        } else {
            // do nothing
        }
    }

    abstract fun getDescription(): String

    override fun toString(): String {
        return "Product(id = $id, name = $name, price = $price)"
    }

    override fun calculateDiscount(amount: Double): Double = amount * 0.1

    open fun calculateDiscount(): Double = calculateDiscount(getPrice())
}