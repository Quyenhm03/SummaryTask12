package com.example.summarytask12.repository

import com.example.summarytask12.extension.matches
import com.example.summarytask12.model.product.Product
import com.example.summarytask12.model.product.ProductCategory
import com.example.summarytask12.util.DatabaseConnect

class ProductRepository {
    val products: MutableMap<String, Product> = mutableMapOf()

    fun save(product: Product): Result<Product> {
        return try {
            products[product.id] = product
            val query =
                "INSERT INTO products (id, name, price, category, stock) VALUES " + "('${product.id}', '${product.name}', ${product.price}, '${product.category}', ${product.stock})"
            DatabaseConnect.query(query)
            println("Product saved: ${product.name}")
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun findById(id: String): Product? = products[id]

    fun findAll(): List<Product> = products.values.toList()

    fun update(product: Product): Result<Product> {
        return if (products.containsKey(product.id)) {
            products[product.id] = product
            val query =
                "UPDATE products SET name='${product.name}', price=${product.price}, stock=${product.stock} WHERE id='${product.id}'"
            DatabaseConnect.query(query)
            Result.success(product)
        } else {
            Result.failure(NoSuchElementException("Product not found"))
        }
    }

    fun search(
        query: String,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        category: ProductCategory? = null
    ): List<Product> {
        return products.values.filter { it.matches(query, minPrice, maxPrice, category) }
    }

    fun findByCategory(category: ProductCategory): List<Product> {
        return products.values.filter { it.category == category }
    }

}