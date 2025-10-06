package com.example.summarytask12.controller

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.StoreApplication
import com.example.summarytask12.service.OrderService
import com.example.summarytask12.util.InputHandler
import com.example.summarytask12.util.OutputHandler

class OrderController(
    private val orderService: OrderService,
    private val inputHandler: InputHandler,
    private val outputHandler: OutputHandler,
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun orderManagement() {
        while (true) {
            outputHandler.printOrderMenu()
            when (inputHandler.readInt("\nOption: ") ?: -1) {
                1 -> {
                    createNewOrder()
                }
                2 -> {
                    viewAllOrders()
                }
                3 -> {
                    findOrder()
                }
                4 -> {
                    showOrderAnalytics()
                }
                5 -> {
                    viewOrderReport()
                }
                6 -> {
                    viewOrderOfCustomer()
                }
                0 -> {
                    return
                }
                else -> {
                    outputHandler.printError("Invalid option")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend private fun createNewOrder() {
        outputHandler.printSuccess("\nCreate New Order")
        val customerId = inputHandler.readLine("Customer ID: ") ?: return
        orderService.createOrder(customerId).onSuccess { order ->
            outputHandler.printSuccess("Order created: ${order.id}")
            while (true) {
                val addItem = inputHandler.readLine("Add item to order? (y/n): ")
                if (addItem?.lowercase() != "y") break
                val productId = inputHandler.readLine("Product ID: ") ?: continue
                val quantity = inputHandler.readInt("Quantity: ") ?: continue
                orderService.addItemToOrder(order.id, productId, quantity)
            }
            val processOrder = inputHandler.readLine("Process order now? (y/n): ")
            if (processOrder?.lowercase() == "y") {
                orderService.processOrder(order.id).onSuccess { processedOrder ->
                    outputHandler.printSuccess("Order processed successfully!")
                    outputHandler.printSuccess("Total Amount: ${processedOrder.getTotalAmount().toInt()} VND")
                    StoreApplication.currentRevenue += processedOrder.getTotalAmount()
                }.onFailure {
                    outputHandler.printError("Error processing order: ${it.message}")
                }
            }
        }.onFailure {
            outputHandler.printError("Error: ${it.message}")
        }
    }

    private fun viewAllOrders() {
        outputHandler.printSuccess("\nAll Orders")
        val orders = orderService.findAll()
        outputHandler.printAllOrders(orders)
    }

    private fun viewOrderOfCustomer() {
        outputHandler.printSuccess("\nView Orders of Customer")
        val customerId = inputHandler.readLine("Customer ID: ") ?: return
        val orders = orderService.getOrdersByCustomer(customerId)
        outputHandler.printAllOrders(orders)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun findOrder() {
        outputHandler.printSuccess("\nFind Order")
        val orderId = inputHandler.readLine("Order ID: ") ?: return
        val order = orderService.findById(orderId)
        if (order != null) {
            outputHandler.printOrderDetails(order)
        } else {
            outputHandler.printError("Order not found")
        }
    }

    private fun showOrderAnalytics() {
        outputHandler.printSuccess("\nOrder Analytics")
        val orders = orderService.findAll()
        val totalRevenue = orderService.getTotalRevenue()
        outputHandler.printOrderAnalytics(orders, totalRevenue)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun viewOrderReport() {
        val orderId = inputHandler.readLine("Enter Order ID to view report: ") ?: return
        val order = orderService.findById(orderId)
        if (order != null) {
            val report = order.generateReport()
            println("\n" + "=".repeat(60))
            println("ORDER REPORT")
            println("=".repeat(60))
            report.forEach { (key, value) ->
                println("${key.padEnd(15)}: $value")
            }
            println("=".repeat(60))
        } else {
            outputHandler.printError("Order not found.")
        }
    }
}