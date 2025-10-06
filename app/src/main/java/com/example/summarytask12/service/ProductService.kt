package com.example.summarytask12.service

import com.example.summarytask12.model.product.Product
import com.example.summarytask12.model.product.ProductCategory
import com.example.summarytask12.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class ProductService(val productRepository: ProductRepository) {

    suspend fun create(product: Product): Result<Product> {
        return productRepository.save(product)
    }

    suspend fun findById(id: String): Product? = productRepository.findById(id)

    suspend fun findAll(): List<Product> = productRepository.findAll()

    suspend fun update(product: Product): Result<Product> {
        return productRepository.update(product)
    }

    suspend fun searchProducts(
        query: String,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        category: ProductCategory? = null
    ): List<Product> {
        return productRepository.search(query, minPrice, maxPrice, category)
    }

    suspend fun updateStock(productId: String, adjustment: Int): Result<Product> {
        return findById(productId)?.let { product ->
            product.updateStock(adjustment).map {
                productRepository.update(product)
                product
            }
        } ?: Result.failure(NoSuchElementException("Product not found"))
    }

    suspend fun generateInventoryReport(): InventoryReport = withContext(Dispatchers.Default) {
        delay(500)

        val allProducts = findAll()
        val categoryBreakdown = allProducts.groupingBy { it.category }.eachCount()

        InventoryReport(
            totalProducts = allProducts.size,
            totalValue = allProducts.sumOf { it.price * it.stock },
            categoryBreakdown = categoryBreakdown
        )
    }

    suspend fun findByCategory(category: ProductCategory): List<Product> {
        return productRepository.findByCategory(category)
    }

    suspend fun updateStockAsync(updates: Map<String, Int>): Result<List<Product>> =
        coroutineScope {
            try {
                val results = updates.map { (productId, adjustment) ->
                    async {
                        updateStock(productId, adjustment)
                    }
                }.awaitAll()

                val failures = results.filter { it.isFailure }
                if (failures.isEmpty()) {
                    Result.success(results.mapNotNull { it.getOrNull() })
                } else {
                    Result.failure(Exception("Some stock updates failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}