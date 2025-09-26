package com.example.summarytask12.service

import com.example.summarytask12.model.product.Product
import com.example.summarytask12.model.product.ProductCategory
import com.example.summarytask12.repository.ProductRepository

class ProductService(val productRepository: ProductRepository) {

    fun create(product: Product): Result<Product> {
        return productRepository.save(product)
    }

    fun findById(id: String): Product? = productRepository.findById(id)

    fun findAll(): List<Product> = productRepository.findAll()

    fun update(product: Product): Result<Product> {
        return productRepository.update(product)
    }

    fun searchProducts(
        query: String,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        category: ProductCategory? = null
    ): List<Product> {
        return productRepository.search(query, minPrice, maxPrice, category)
    }

    fun updateStock(productId: String, adjustment: Int): Result<Product> {
        return findById(productId)?.let { product ->
            product.updateStock(adjustment).map {
                productRepository.update(product)
                product
            }
        } ?: Result.failure(NoSuchElementException("Product not found"))
    }

    fun generateInventoryReport(): InventoryReport {
        val allProducts = findAll()
        val categoryBreakdown = allProducts.groupingBy { it.category }.eachCount()

        return InventoryReport(
            totalProducts = allProducts.size,
            totalValue = allProducts.sumOf { it.price * it.stock },
            categoryBreakdown = categoryBreakdown
        )
    }
}