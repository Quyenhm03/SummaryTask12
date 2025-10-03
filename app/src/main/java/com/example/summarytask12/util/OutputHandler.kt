package com.example.summarytask12.util

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.model.customer.Customer
import com.example.summarytask12.model.order.Order
import com.example.summarytask12.model.product.Product
import com.example.summarytask12.service.InventoryReport
import com.example.summarytask12.service.SalesReport
import java.time.format.DateTimeFormatter

class OutputHandler {
    fun printMainMenu(currentRevenue: Double) {
        println("\n" + "=".repeat(60))
        println(" STORE MANAGEMENT SYSTEM")
        println("=".repeat(60))
        println("1. Product Management")
        println("2. Customer Management")
        println("3. Order Management")
        println("4. Reports & Analytics")
        println("5. Exit")
        println("=".repeat(60))
        println("Current Revenue: ${currentRevenue.toInt()} VND")
    }

    fun printExitMessage(currentRevenue: Double) {
        println("Shutting down system...")
        println("Final Revenue: ${currentRevenue.toInt()} VND")
        println("Thank you for using Store Management System!")
    }

    fun printError(message: String) {
        println(message)
    }

    fun printContinuePrompt() {
        println("\nPress Enter to continue...")
    }

    fun printProductMenu() {
        println("\n=== PRODUCT MANAGEMENT ===")
        println("1. Add Electronic Product")
        println("2. Add Clothing Product")
        println("3. Search Products")
        println("4. Update Product Stock")
        println("5. Display Product Details")
        println("6. Product Analytics")
        println("0. Return to Main Menu")
    }

    fun printCustomerMenu() {
        println("\n=== CUSTOMER MANAGEMENT ===")
        println("1. Create New Customer")
        println("2. Find Customer")
        println("3. View Customer Type")
        println("0. Return to Main Menu")
    }

    fun printOrderMenu() {
        println("\n=== ORDER MANAGEMENT ===")
        println("1. Create New Order")
        println("2. View All Orders")
        println("3. Find Order")
        println("4. Order Analytics")
        println("0. Return to Main Menu")
    }

    fun printReportsMenu() {
        println("\n=== REPORTS & ANALYTICS ===")
        println("1. Inventory Report")
        println("2. Sales Report")
        println("3. Revenue Summary")
        println("0. Return to Main Menu")
    }

    fun printProductDetails(product: Product) {
        println("\n" + "=".repeat(60))
        println("PRODUCT DETAILS")
        println("=".repeat(60))
        println("ID: ${product.id}")
        println("Name: ${product.name}")
        println("Category: ${product.category.displayName}")
        println("Price: ${product.price.toInt()} VND")
        println("Stock: ${product.stock}")
        println("\nDescription:")
        println(product.generateDescription())
        println("=".repeat(60))
    }

    fun printProductSearchResults(results: List<Product>) {
        if (results.isEmpty()) {
            println("No products found")
        } else {
            println("\nFound ${results.size} products:")
            println("-".repeat(80))
            results.forEachIndexed { index, product ->
                println("${index + 1}. ${product.name}")
                println("   ID: ${product.id} | Price: ${product.price.toInt()} VND | Stock: ${product.stock}")
                println("   Category: ${product.category.displayName}")
                println("-".repeat(80))
            }
        }
    }

    fun printCustomerDetails(customer: Customer) {
        println("\n" + "=".repeat(60))
        println("CUSTOMER DETAILS")
        println("=".repeat(60))
        println("ID: ${customer.id}")
        println("Name: ${customer.name}")
        println("Email: ${customer.email}")
        customer.phone?.let { println("Phone: $it") }
        println("\nMEMBERSHIP INFO")
        println("Customer Type: ${customer.customerType.displayName} (${(customer.customerType.discountRate * 100).toInt()}% discount)")
        println("Total Spent: ${customer.totalSpent.toInt()} VND")
        println("Total Orders: ${customer.orderHistory.size}")
        println("=".repeat(60))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun printOrderDetails(order: Order) {
        println("\n" + "=".repeat(60))
        println("ORDER DETAILS")
        println("=".repeat(60))
        println("Order ID: ${order.id}")
        println("Customer: ${order.customer.name}")
        println("Status: ${order.status}")
        println("Order Date: ${order.orderDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))}")
        println("\nORDER ITEMS")
        println("-".repeat(40))
        order.items.forEach { item ->
            println("${item.product.name} x${item.quantity} @ ${item.unitPrice.toInt()} VND")
        }
        println("\nORDER SUMMARY")
        println("-".repeat(40))
        println("Subtotal: ${order.getSubtotal().toInt()} VND")
        println("Total Discount: -${order.getTotalDiscount().toInt()} VND")
        println("TOTAL: ${order.getTotalAmount().toInt()} VND")
        println("=".repeat(60))
    }

    fun printInventoryReport(report: InventoryReport) {
        println("\n" + "=".repeat(60))
        println("INVENTORY SUMMARY")
        println("=".repeat(60))
        println("Total Products: ${report.totalProducts}")
        println("Total Inventory Value: ${report.totalValue.toInt()} VND")
        println("Categories: ${report.categoryBreakdown.size}")
        println("\nBY CATEGORY")
        println("-".repeat(40))
        report.categoryBreakdown.forEach { (category, count) ->
            println("${category.displayName}: $count products")
        }
        println("=".repeat(60))
    }

    fun printSalesReport(report: SalesReport) {
        println("\n" + "=".repeat(60))
        println("SALES SUMMARY")
        println("=".repeat(60))
        println("Period: ${report.period}")
        println("Total Orders: ${report.totalOrders}")
        println("Total Revenue: ${report.totalRevenue.toInt()} VND")
        if (report.totalOrders > 0) {
            val avgOrderValue = report.totalRevenue / report.totalOrders
            println("Average Order Value: ${avgOrderValue.toInt()} VND")
        }
        println("=".repeat(60))
    }

    fun printRevenueSummary(totalRevenue: Double, orders: List<Order>) {
        println("\n" + "=".repeat(60))
        println("REVENUE OVERVIEW")
        println("=".repeat(60))
        println("System Revenue: ${totalRevenue.toInt()} VND")
        println("Total Delivered Revenue: ${totalRevenue.toInt()} VND")
        println("Total Orders: ${orders.size}")
        println("\nBY STATUS")
        println("-".repeat(40))
        orders.groupingBy { it.status }.eachCount().forEach { (status, count) ->
            println("${status}: $count orders")
        }
        println("=".repeat(60))
    }

    fun printProductAnalytics(report: InventoryReport) {
        println("\n" + "=".repeat(60))
        println("INVENTORY OVERVIEW")
        println("=".repeat(60))
        println("Total Products: ${report.totalProducts}")
        println("Total Value: ${report.totalValue.toInt()} VND")
        println("=".repeat(60))
    }

    fun printOrderAnalytics(orders: List<Order>, totalRevenue: Double) {
        println("\n" + "=".repeat(60))
        println("ORDER STATISTICS")
        println("=".repeat(60))
        println("Total Orders: ${orders.size}")
        println("Total Revenue: ${totalRevenue.toInt()} VND")
        println("=".repeat(60))
    }

    fun printCustomerTypes(customerService: com.example.summarytask12.service.CustomerService) {
        println("\nCustomer Type")
        com.example.summarytask12.model.customer.CustomerType.values().forEach { type ->
            val customers = customerService.getCustomersByType(type)
            println("\n${type.displayName} (${(type.discountRate * 100).toInt()}% discount)")
            println("  Minimum spending: ${type.minSpending.toInt()} VND")
            println("  Customers: ${customers.size}")
        }
    }

    fun printAllOrders(orders: List<Order>) {
        if (orders.isEmpty()) {
            println("No orders found")
        } else {
            println("\nFound ${orders.size} orders:")
            println("-".repeat(80))
            orders.forEach { order ->
                println("${order.id} | ${order.customer.name} | ${order.status} | ${order.getTotalAmount().toInt()} VND")
            }
            println("-".repeat(80))
        }
    }

    fun printSuccess(message: String) {
        println(message)
    }
}