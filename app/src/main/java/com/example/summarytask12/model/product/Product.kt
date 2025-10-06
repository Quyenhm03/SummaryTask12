package com.example.summarytask12.model.product

abstract class Product(
    val id: String,
    val name: String,
    var price: Double,
    val category: ProductCategory,
    var stock: Int = 0
) {
    var description: String = ""
        get() = generateDescription()

    fun updateStock(newStock: Int): Result<Unit> {
        return if (stock + newStock >= 0) {
            stock += newStock
            Result.success(Unit)
        } else {
            Result.failure(IllegalStateException("Stock not enough"))
        }
    }

    override fun toString(): String {
        return "Product(id='$id', name='$name', price=${price.toInt()}, stock=$stock, category=$category)"
    }

    abstract fun generateDescription(): String
    abstract fun calculateDiscountProduct(amount: Double): Double
}