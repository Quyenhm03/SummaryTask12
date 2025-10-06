package com.example.summarytask12

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.controller.CustomerController
import com.example.summarytask12.controller.MenuController
import com.example.summarytask12.controller.OrderController
import com.example.summarytask12.controller.ProductController
import com.example.summarytask12.controller.ReportController
import com.example.summarytask12.model.product.Clothing
import com.example.summarytask12.model.product.ClothingSize
import com.example.summarytask12.model.product.Electronic
import com.example.summarytask12.repository.CustomerRepository
import com.example.summarytask12.repository.OrderRepository
import com.example.summarytask12.repository.ProductRepository
import com.example.summarytask12.service.CustomerService
import com.example.summarytask12.service.OrderService
import com.example.summarytask12.service.ProductService
import com.example.summarytask12.util.DatabaseConnect
import com.example.summarytask12.util.InputHandler
import com.example.summarytask12.util.OutputHandler

class StoreApplication {
    companion object {
        var currentRevenue: Double = 0.0
    }

    val productRepository = ProductRepository()
    val customerRepository = CustomerRepository()
    val orderRepository = OrderRepository()

    val productService = ProductService(productRepository)
    val customerService = CustomerService(customerRepository)
    val orderService = OrderService(orderRepository, productService, customerService)

    val inputHandler = InputHandler()
    val outputHandler = OutputHandler()

    val productController = ProductController(productService, inputHandler, outputHandler)
    val customerController = CustomerController(customerService, inputHandler, outputHandler)
    val reportController =
        ReportController(productService, orderService, inputHandler, outputHandler)
    val orderController = OrderController(orderService, inputHandler, outputHandler)

    val menuController = MenuController(
        productController,
        customerController,
        orderController,
        reportController,
        inputHandler,
        outputHandler
    )

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun run() {
        println("=== Welcome to Store Management System ===")

        DatabaseConnect.connect()

        try {
            initializeData()
            menuController.showMainMenu()
        } catch (e: Exception) {
            println("System error: ${e.message}")
        } finally {
            DatabaseConnect.disconnect()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun initializeData() {
        println("Initializing data...")

        val laptop = Electronic("ELEC-001", "Dell XPS 13", 25000000.0, 24).apply {
            updateStock(15)
        }
        productService.create(laptop)

        val smartphone = Electronic("ELEC-002", "iPhone 14", 22000000.0, 12).apply {
            updateStock(8)
        }
        productService.create(smartphone)

        val shirt =
            Clothing("CLOTH-001", "Business Shirt", 450000.0, ClothingSize.M, "Premium").apply {
                updateStock(30)
                season = "All Season"
                gender = "Men"
            }
        productService.create(shirt)

        val jeans =
            Clothing("CLOTH-002", "Slim Fit Jeans", 890000.0, ClothingSize.L, "Levi's").apply {
                updateStock(20)
                gender = "Unisex"
            }
        productService.create(jeans)

        customerService.createCustomer("Nguyen Van An", "an.nguyen@email.com", "0123456789")
        customerService.createCustomer("Tran Thi Binh", "binh.tran@email.com", "0987654321")
        customerService.createCustomer("Le Van Cuong", "cuong.le@email.com", "0369852741")

        println("Sample data initialized successfully!")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
suspend fun main() {
    StoreApplication().run()
}