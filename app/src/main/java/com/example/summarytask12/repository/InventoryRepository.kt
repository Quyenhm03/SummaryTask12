package com.example.summarytask12.repository

import com.example.summarytask12.model.product.Clothing
import com.example.summarytask12.model.product.Electronics
import com.example.summarytask12.model.product.Product
import com.example.summarytask12.util.DatabaseConnect

class InventoryRepository {
    private val products: MutableList<Product> = mutableListOf()

    init {
        if (!DatabaseConnect.isConnected()) {
            throw IllegalStateException("Database not connected")
        }
    }

    fun addProduct(product: Product) {
        products.add(product)
        println("Added: ${product.getPropertyName()} - ${product.getPropertyPrice()}")

        DatabaseConnect.query("INSERT INTO products(id, name, price, stock, description) " +
                "VALUES ('${product.getPropertyId()}', '${product.getPropertyName()}', ${product.getPropertyPrice()}, ${product.getPropertyStock()}, '${product.getDescription()}')")

        when(product) {
            is Electronics -> {
                DatabaseConnect.query("INSERT INTO electronics(id, warrantyYears) VALUES ('${product.getPropertyId()}', ${product.getWarrantyYears() ?: "0"})")
            }
            is Clothing -> {
                DatabaseConnect.query("INSERT INTO clothing(id, size) VALUES ('${product.getPropertyId()}', '${product.getSize().name}')")
            }
        }
    }

    fun findById(id: String) : Product? {
        DatabaseConnect.query("SELECT p.id, p.name, p.price, p.stock, p.description, e.warrantyYears, c.size " +
                                        "FROM products p " +
                                        "LEFT JOIN electronics e ON p.id = e.id " +
                                        "LEFT JOIN clothing c ON p.id = c.id " +
                                        "WHERE p.id = '$id'")
        return products.find { it.getPropertyId() == id }
    }

    fun searchProducts(query: String, minPrice: Double = 0.0, category: String? = null) : List<Product> {
        return products.filter { prod ->
            prod.getPropertyName().contains(query, ignoreCase = true) &&
            prod.getPropertyPrice() >= minPrice &&
            (category == null || when (prod) {
                is Electronics -> category == "Electronics"
                is Clothing -> category == "Clothing"
                else -> false
            })
        }
    }

    fun updateStock(productId: String, newStock: Int) {
        val product = findById(productId)
        if (product != null && newStock >= 0) {
            product.updateStock(newStock)
            DatabaseConnect.query("UPDATE products SET stock=${product.getPropertyStock()} WHERE id='$productId'")
        }
    }

    fun checkLowStock(product: Product?): Boolean {
        val p = product ?: return false
        return p.getPropertyStock() < 5
    }

    fun getTotalInventoryValue() = products.sumOf { it.getPropertyPrice() * it.getPropertyStock() }
}