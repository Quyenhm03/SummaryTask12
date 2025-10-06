package com.example.summarytask12.repository

import com.example.summarytask12.extension.matches
import com.example.summarytask12.model.product.Clothing
import com.example.summarytask12.model.product.Electronic
import com.example.summarytask12.model.product.Product
import com.example.summarytask12.model.product.ProductCategory
import com.example.summarytask12.util.DatabaseConnect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ProductRepository {
    val products: MutableMap<String, Product> = mutableMapOf()
    private val dispatcher = Dispatchers.IO

    suspend fun save(product: Product): Result<Product> = withContext(dispatcher) {
        try {
            delay(500)

            products[product.id] = product
            val queryProduct = "INSERT INTO products (id, name, price, category, stock, description) VALUES " +
                        "('${product.id}', '${product.name}', ${product.price}, '${product.category}', '${product.stock}', '${product.description}')"
            DatabaseConnect.query(queryProduct)

            when (product) {
                is Electronic -> {
                    delay(200)
                    val queryElectronic = "INSERT INTO electronics (productId, warrantyMonths) VALUES " +
                                "('${product.id}', ${product.warrantyMonths})"
                    DatabaseConnect.query(queryElectronic)
                }
                is Clothing -> {
                    delay(200)
                    val queryClothing = "INSERT INTO clothing (productId, size, brand) VALUES " +
                                "('${product.id}', '${product.size}', '${product.brand}')"
                    DatabaseConnect.query(queryClothing)
                }
            }

            println("Product saved: ${product.name}")
            Result.success(product)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findById(id: String): Product? = withContext(dispatcher) {
        delay(300)

        val query = "SELECT p.id, p.name, p.price, p.category, p.stock, p.description, e.warrantMonths, c. brand, c.size " +
                    "FROM products p " +
                    "LEFT JOIN electronics e ON p.id = e.productId " +
                    "LEFT JOIN clothing c ON p.id = c.productId " +
                    "WHERE p.id = '$id'"
        DatabaseConnect.query(query)
        products[id]
    }

    suspend fun findAll(): List<Product> = withContext(dispatcher) {
        delay(500)

        val query = "SELECT p.id, p.name, p.price, p.category, p.stock, p.description, e.warrantMonths, c. brand, c.size " +
                    "FROM products p " +
                    "LEFT JOIN electronics e ON p.id = e.productId " +
                    "LEFT JOIN clothing c ON p.id = c.productId "
        DatabaseConnect.query(query)
        products.values.toList()
    }

    suspend fun update(product: Product): Result<Product> = withContext(dispatcher) {
        if (products.containsKey(product.id)) {
            delay(400)

            products[product.id] = product
            val queryProduct =
                "UPDATE products SET name='${product.name}', price=${product.price}, stock=${product.stock}, description=${product.description} " +
                        "WHERE id='${product.id}'"
            DatabaseConnect.query(queryProduct)

            when (product) {
                is Electronic -> {
                    delay(200)
                    val queryElectronic =
                        "UPDATE electronics SET warrantyMonths=${product.warrantyMonths} " +
                                "WHERE product_id='${product.id}'"
                    DatabaseConnect.query(queryElectronic)
                }
                is Clothing -> {
                    delay(200)
                    val queryClothing =
                        "UPDATE clothing SET brand='${product.brand}', size='${product.size}' " +
                                "WHERE product_id='${product.id}'"
                    DatabaseConnect.query(queryClothing)
                }
            }

            Result.success(product)
        } else {
            Result.failure(NoSuchElementException("Product not found"))
        }
    }

    suspend fun search(
        query: String,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        category: ProductCategory? = null
    ): List<Product> = withContext(dispatcher) {
        delay(600)

        var querySearch = "SELECT p.id, p.name, p.price, p.category, p.stock, p.description, " +
                    "e.warrantyMonths, c.brand, c.size " +
                    "FROM products p " +
                    "LEFT JOIN electronics e ON p.id = e.productId " +
                    "LEFT JOIN clothing c ON p.id = c.productId " +
                    "WHERE 1=1 "

        if (query.isNotBlank()) {
            querySearch += "AND (p.name LIKE '%$query%' OR p.description LIKE '%$query%') "
        }

        if (minPrice != null) {
            querySearch += "AND p.price >= $minPrice "
        }

        if (maxPrice != null) {
            querySearch += "AND p.price <= $maxPrice "
        }

        if (category != null) {
            querySearch += "AND p.category = '${category.name}' "
        }

        DatabaseConnect.query(querySearch)
        products.values.filter { it.matches(query, minPrice, maxPrice, category) }
    }

    suspend fun findByCategory(category: ProductCategory): List<Product> = withContext(dispatcher) {
        delay(400)

        when (category) {
            ProductCategory.ELECTRONIC -> {
                val query = "SELECT p.id, p.name, p.price, p.category, p.stock, p.description, e.warrantMonths " +
                            "FROM products p " +
                            "LEFT JOIN electronics e ON p.id = e.productId " +
                            "WHERE p.category =  '${category}'"
                DatabaseConnect.query(query)
            }

            ProductCategory.CLOTHING -> {
                val query = "SELECT p.id, p.name, p.price, p.category, p.stock, p.description, c. brand, c.size " +
                            "FROM products p " +
                            "LEFT JOIN clothing c ON p.id = c.productId " +
                            "WHERE p.category =  '${category}'"
                DatabaseConnect.query(query)
            }
        }
        products.values.filter { it.category == category }
    }

    suspend fun saveBatch(productList: List<Product>) :  Result<List<Product>> = withContext(dispatcher) {
        try {
            val results = productList.map { product ->
                async {
                    save(product)
                }
            }.awaitAll()

            val failures = results.filter {
                it.isFailure
            }
            if (failures.isEmpty()) {
                Result.success(productList)
            } else {
                Result.failure(Exception("Some products failed to save!"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}