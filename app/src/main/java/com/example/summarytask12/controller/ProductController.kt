package com.example.summarytask12.controller

import com.example.summarytask12.model.product.Clothing
import com.example.summarytask12.model.product.ClothingSize
import com.example.summarytask12.model.product.Electronic
import com.example.summarytask12.model.product.ProductCategory
import com.example.summarytask12.service.ProductService
import com.example.summarytask12.util.InputHandler
import com.example.summarytask12.util.OutputHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ProductController(
    private val productService: ProductService,
    private val inputHandler: InputHandler,
    private val outputHandler: OutputHandler
) {

    fun productManagement() = runBlocking {
        while (true) {
            outputHandler.printProductMenu()
            when (inputHandler.readInt("\nOption: ") ?: -1) {
                1 -> {
                    addElectronicProduct()
                }
                2 -> {
                    addClothingProduct()
                }
                3 -> {
                    searchProducts()
                }
                4 -> {
                    findProductsByCategory()
                }
                5 -> {
                    updateProductStock()
                }
                6 -> {
                    displayProductDetails()
                }
                7 -> {
                    showProductAnalytics()
                }
                8 -> {
                    updateStockAsync()
                }
                0 -> {
                    return@runBlocking
                }
                else -> {
                    outputHandler.printError("Invalid option")
                }
            }
        }
    }

    private suspend fun addElectronicProduct() {
        outputHandler.printSuccess("\nAdd Electronic Product")
        val id = inputHandler.readLine("Product ID: ") ?: return
        val name = inputHandler.readLine("Product Name: ") ?: return
        val price = inputHandler.readDouble("Price (VND): ") ?: return
        val warranty = inputHandler.readInt("Warranty months: ")
        val stock = inputHandler.readInt("Initial Stock: ") ?: 0

        try {
            val product = Electronic(id, name, price, warranty)
            product.updateStock(stock)

            outputHandler.printSuccess("Saving to database ...")
            productService.create(product).onSuccess {
                outputHandler.printSuccess("Electronic product added successfully!")
            }.onFailure {
                outputHandler.printError("Error: ${it.message}")
            }
        } catch (e: Exception) {
            outputHandler.printError("Error creating product: ${e.message}")
        }
    }

    private suspend fun addClothingProduct() {
        outputHandler.printSuccess("\nAdd Clothing Product")
        val id = inputHandler.readLine("Product ID: ") ?: return
        val name = inputHandler.readLine("Product Name: ") ?: return
        val price = inputHandler.readDouble("Price (VND): ") ?: return
        val brand = inputHandler.readLine("Brand (default Generic): ") ?: "Generic"
        outputHandler.printSuccess("Available sizes: ${ClothingSize.entries.joinToString(", ") { "${it.name} (${it.displayName})" }}")
        val sizeInput = inputHandler.readLine("Size: ")?.uppercase() ?: return
        val size = try {
            ClothingSize.valueOf(sizeInput)
        } catch (e: Exception) {
            outputHandler.printError("Invalid size")
            return
        }
        val stock = inputHandler.readInt("Initial Stock: ") ?: 0

        try {
            val product = Clothing(id, name, price, size, brand)
            product.updateStock(stock)

            outputHandler.printSuccess("Saving to database ...")
            productService.create(product).onSuccess {
                outputHandler.printSuccess("Clothing product added successfully!")
            }.onFailure {
                outputHandler.printError("Error: ${it.message}")
            }
        } catch (e: Exception) {
            outputHandler.printError("Error creating product: ${e.message}")
        }
    }

    private suspend fun searchProducts() {
        outputHandler.printSuccess("\nProduct Search")
        val query = inputHandler.readLine("Search query: ") ?: return
        val minPrice = inputHandler.readDouble("Minimum price (optional): ")
        val maxPrice = inputHandler.readDouble("Maximum price (optional): ")
        outputHandler.printSuccess("Categories: ${ProductCategory.entries.joinToString(", ") { it.displayName }}")
        val categoryInput = inputHandler.readLine("Category (optional): ")
        val category = ProductCategory.entries.find {
            it.displayName.equals(categoryInput, ignoreCase = true)
        }

        outputHandler.printSuccess("Searching database ...")
        val results = productService.searchProducts(query, minPrice, maxPrice, category)
        outputHandler.printProductSearchResults(results)
    }

    private suspend fun updateProductStock() {
        outputHandler.printSuccess("\nUpdate Product Stock")
        val productId = inputHandler.readLine("Product ID: ") ?: return

        outputHandler.printSuccess("Loading product...")
        val product = productService.findById(productId)
        if (product == null) {
            outputHandler.printError("Product not found")
            return
        }

        outputHandler.printSuccess("Current product: ${product.name}")
        outputHandler.printSuccess("Current stock: ${product.stock}")
        val adjustment = inputHandler.readInt("Stock adjustment (+ to add, - to remove): ") ?: return

        outputHandler.printSuccess("Updating stock...")
        productService.updateStock(productId, adjustment).onSuccess { updatedProduct ->
            outputHandler.printSuccess("Stock updated successfully!")
            outputHandler.printSuccess("New stock level: ${updatedProduct.stock}")
        }.onFailure {
            outputHandler.printError("Error: ${it.message}")
        }
    }

    private suspend fun displayProductDetails() {
        outputHandler.printSuccess("\nProduct Details")
        val productId = inputHandler.readLine("Product ID: ") ?: return

        outputHandler.printSuccess("Loading product details ...")
        val product = productService.findById(productId)
        if (product == null) {
            outputHandler.printError("Product not found")
            return
        }
        outputHandler.printProductDetails(product)
    }

    private suspend fun showProductAnalytics() {
        outputHandler.printSuccess("\nProduct Analytics")
        outputHandler.printSuccess("Generating inventory report ...")
        val report = productService.generateInventoryReport()
        outputHandler.printProductAnalytics(report)
    }

    private suspend fun findProductsByCategory() {
        outputHandler.printSuccess("\nFind Products by Category")
        outputHandler.printSuccess("Available categories: ${ProductCategory.entries.joinToString(", ") { it.displayName }}")
        val categoryInput = inputHandler.readLine("Category: ") ?: return
        val category = ProductCategory.entries.find {
            it.displayName.equals(categoryInput, ignoreCase = true)
        }
        if (category == null) {
            outputHandler.printError("Invalid category")
            return
        }

        outputHandler.printSuccess("Loading products ...")
        val results = productService.findByCategory(category)
        if (results.isEmpty()) {
            outputHandler.printError("No products found for category: ${category.displayName}")
        } else {
            outputHandler.printProductSearchResults(results)
        }
    }

    private suspend fun updateStockAsync() {
        outputHandler.printSuccess("\nUpdate Stock Async")
        val updates = mutableMapOf<String, Int>()

        while (true) {
            val productId = inputHandler.readLine("Product ID (empty to finish): ") ?: break
            val adjustment = inputHandler.readInt("Adjustment: ") ?: continue

            updates[productId] = adjustment
        }

        if (updates.isEmpty()) {
            outputHandler.printError("No updates")
            return
        }

        outputHandler.printSuccess("Processing ${updates.size} updates in parallel ...")
        productService.updateStockAsync(updates).onSuccess { updatedProducts ->
            outputHandler.printSuccess("Successfully updated ${updatedProducts.size} products!")
        }.onFailure {
            outputHandler.printError("Error: ${it.message}")
        }
    }
}
