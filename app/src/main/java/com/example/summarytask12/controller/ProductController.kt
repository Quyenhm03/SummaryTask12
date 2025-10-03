package com.example.summarytask12.controller

import com.example.summarytask12.model.product.Clothing
import com.example.summarytask12.model.product.ClothingSize
import com.example.summarytask12.model.product.Electronic
import com.example.summarytask12.model.product.ProductCategory
import com.example.summarytask12.service.ProductService
import com.example.summarytask12.util.InputHandler
import com.example.summarytask12.util.OutputHandler

class ProductController(
    private val productService: ProductService,
    private val inputHandler: InputHandler,
    private val outputHandler: OutputHandler
) {

    fun productManagement() {
        while (true) {
            outputHandler.printProductMenu()
            when (inputHandler.readInt("\nOption: ") ?: -1) {
                1 -> addElectronicProduct()
                2 -> addClothingProduct()
                3 -> searchProducts()
                4 -> updateProductStock()
                5 -> displayProductDetails()
                6 -> showProductAnalytics()
                0 -> return
                else -> outputHandler.printError("Invalid option")
            }
        }
    }

    private fun addElectronicProduct() {
        outputHandler.printSuccess("\nAdd Electronic Product")
        val id = inputHandler.readLine("Product ID: ") ?: return
        val name = inputHandler.readLine("Product Name: ") ?: return
        val price = inputHandler.readDouble("Price (VND): ") ?: return
        val warranty = inputHandler.readInt("Warranty months: ")
        val stock = inputHandler.readInt("Initial Stock: ") ?: 0

        try {
            val product = Electronic(id, name, price, warranty)
            product.updateStock(stock)
            productService.create(product).onSuccess {
                outputHandler.printSuccess("Electronic product added successfully!")
            }.onFailure {
                outputHandler.printError("Error: ${it.message}")
            }
        } catch (e: Exception) {
            outputHandler.printError("Error creating product: ${e.message}")
        }
    }

    private fun addClothingProduct() {
        outputHandler.printSuccess("\nAdd Clothing Product")
        val id = inputHandler.readLine("Product ID: ") ?: return
        val name = inputHandler.readLine("Product Name: ") ?: return
        val price = inputHandler.readDouble("Price (VND): ") ?: return
        val brand = inputHandler.readLine("Brand (default Generic): ") ?: "Generic"
        outputHandler.printSuccess("Available sizes: ${ClothingSize.values().joinToString(", ") { "${it.name} (${it.displayName})" }}")
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
            productService.create(product).onSuccess {
                outputHandler.printSuccess("Clothing product added successfully!")
            }.onFailure {
                outputHandler.printError("Error: ${it.message}")
            }
        } catch (e: Exception) {
            outputHandler.printError("Error creating product: ${e.message}")
        }
    }

    private fun searchProducts() {
        outputHandler.printSuccess("\nProduct Search")
        val query = inputHandler.readLine("Search query: ") ?: return
        val minPrice = inputHandler.readDouble("Minimum price (optional): ")
        val maxPrice = inputHandler.readDouble("Maximum price (optional): ")
        outputHandler.printSuccess("Categories: ${ProductCategory.values().joinToString(", ") { it.displayName }}")
        val categoryInput = inputHandler.readLine("Category (optional): ")
        val category = ProductCategory.values().find {
            it.displayName.equals(categoryInput, ignoreCase = true)
        }
        val results = productService.searchProducts(query, minPrice, maxPrice, category)
        outputHandler.printProductSearchResults(results)
    }

    private fun updateProductStock() {
        outputHandler.printSuccess("\nUpdate Product Stock")
        val productId = inputHandler.readLine("Product ID: ") ?: return
        val product = productService.findById(productId)
        if (product == null) {
            outputHandler.printError("Product not found")
            return
        }
        outputHandler.printSuccess("Current product: ${product.name}")
        outputHandler.printSuccess("Current stock: ${product.stock}")
        val adjustment = inputHandler.readInt("Stock adjustment (+ to add, - to remove): ") ?: return
        productService.updateStock(productId, adjustment).onSuccess { updatedProduct ->
            outputHandler.printSuccess("Stock updated successfully!")
            outputHandler.printSuccess("New stock level: ${updatedProduct.stock}")
        }.onFailure {
            outputHandler.printError("Error: ${it.message}")
        }
    }

    private fun displayProductDetails() {
        outputHandler.printSuccess("\nProduct Details")
        val productId = inputHandler.readLine("Product ID: ") ?: return
        val product = productService.findById(productId)
        if (product == null) {
            outputHandler.printError("Product not found")
            return
        }
        outputHandler.printProductDetails(product)
    }

    private fun showProductAnalytics() {
        outputHandler.printSuccess("\nProduct Analytics")
        val report = productService.generateInventoryReport()
        outputHandler.printProductAnalytics(report)
    }
}
