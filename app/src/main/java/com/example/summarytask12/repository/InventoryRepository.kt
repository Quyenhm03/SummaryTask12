package com.example.summarytask12.repository

import com.example.summarytask12.model.product.Clothing
import com.example.summarytask12.model.product.Electronics
import com.example.summarytask12.model.product.Product
import com.example.summarytask12.util.ClothingSize
import com.example.summarytask12.util.DatabaseConnect

class InventoryRepository {
    private val products: MutableList<Product> = mutableListOf()

    fun getProducts() = products

    init {
        if (!DatabaseConnect.isConnected()) {
            throw IllegalStateException("Database not connected")
        }
    }

    fun addProduct(product: Product) {
        products.add(product)
        println("Added: ${product.getName()} - ${product.getPrice()}")
        DatabaseConnect.query("INSERT INTO products(id, name, price, stock, description) " +
                "VALUES ('${product.getId()}', '${product.getName()}', ${product.getPrice()}, ${product.getStock()}, '${product.getDescription()}')")

        when(product) {
            is Electronics -> {
                DatabaseConnect.query("INSERT INTO electronics(id, warrantyYears) VALUES ('${product.getId()}', ${product.getWarrantyYears() ?: "0"})")
            }
            is Clothing -> {
                DatabaseConnect.query("INSERT INTO clothing(id, size) VALUES ('${product.getId()}', '${product.getSize().name}')")
            }
        }
    }
}