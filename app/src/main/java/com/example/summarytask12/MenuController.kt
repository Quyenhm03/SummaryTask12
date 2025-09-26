package com.example.summarytask12

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.extension.isValidEmail
import com.example.summarytask12.model.customer.Customer
import com.example.summarytask12.model.customer.CustomerType
import com.example.summarytask12.model.order.Order
import com.example.summarytask12.model.product.Clothing
import com.example.summarytask12.model.product.ClothingSize
import com.example.summarytask12.model.product.Electronic
import com.example.summarytask12.model.product.ProductCategory
import com.example.summarytask12.service.CustomerService
import com.example.summarytask12.service.OrderService
import com.example.summarytask12.service.ProductService
import com.example.summarytask12.util.DatabaseConnect
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

class MenuController(
    val productService: ProductService,
    val customerService: CustomerService,
    val orderService: OrderService
) {

    var currentRevenue: Double = 0.0

    @RequiresApi(Build.VERSION_CODES.O)
    fun showMainMenu() {
        while (true) {
            printMainMenu()
            val choice = readInput("Choose an option (1-5): ")?.toIntOrNull() ?: 0

            when (choice) {
                1 -> productManagement()
                2 -> customerManagement()
                3 -> orderManagement()
                4 -> reportsAndAnalytics()
                5 -> {
                    println("Shutting down system...")
                    DatabaseConnect.disconnect()
                    println("Final Revenue: ${currentRevenue.toInt()} VND")
                    println("Thank you for using Store Management System!")
                    exitProcess(0)
                }

                else -> println("Invalid option, please try again.")
            }

            println("\nPress Enter to continue...")
            readln()
        }
    }

    fun printMainMenu() {
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

    // ============= PRODUCT MANAGEMENT =============

    fun productManagement() {
        while (true) {
            println("\n=== PRODUCT MANAGEMENT ===")
            println("1. Add Electronic Product")
            println("2. Add Clothing Product")
            println("3. Search Products")
            println("4. Update Product Stock")
            println("5. Display Product Details")
            println("6. Product Analytics")
            println("0. Return to Main Menu")

            when (readInput("\nOption: ")?.toIntOrNull() ?: -1) {
                1 -> addElectronicProduct()
                2 -> addClothingProduct()
                3 -> searchProducts()
                4 -> updateProductStock()
                5 -> displayProductDetails()
                6 -> showProductAnalytics()
                0 -> return
                else -> println("Invalid option")
            }
        }
    }

    fun addElectronicProduct() {
        println("\nAdd Electronic Product")

        val id = readInput("Product ID: ") ?: return
        val name = readInput("Product Name: ") ?: return
        val price = readInput("Price (VND): ")?.toDoubleOrNull() ?: return
        val warranty = readInput("Warranty months: ")?.toIntOrNull()
        val stock = readInput("Initial Stock: ")?.toIntOrNull() ?: 0

        try {
            val product = Electronic(id, name, price, warranty)
            product.updateStock(stock)

            productService.create(product).onSuccess {
                println("Electronic product added successfully!")
            }.onFailure {
                println("Error: ${it.message}")
            }
        } catch (e: Exception) {
            println("Error creating product: ${e.message}")
        }
    }

    fun addClothingProduct() {
        println("\nAdd Clothing Product")

        val id = readInput("Product ID: ") ?: return
        val name = readInput("Product Name: ") ?: return
        val price = readInput("Price (VND): ")?.toDoubleOrNull() ?: return
        val brand = readInput("Brand (default Generic): ") ?: "Generic"

        println(
            "Available sizes: ${
                ClothingSize.values().joinToString(", ") { "${it.name} (${it.displayName})" }
            }"
        )
        val sizeInput = readInput("Size: ")?.uppercase() ?: return
        val size = try {
            ClothingSize.valueOf(sizeInput)
        } catch (e: Exception) {
            println("Invalid size")
            return
        }

        val stock = readInput("Initial Stock: ")?.toIntOrNull() ?: 0

        try {
            val product = Clothing(id, name, price, size, brand)
            product.updateStock(stock)

            productService.create(product).onSuccess {
                println("Clothing product added successfully!")
            }.onFailure {
                println("Error: ${it.message}")
            }
        } catch (e: Exception) {
            println("Error creating product: ${e.message}")
        }
    }

    fun searchProducts() {
        println("\nProduct Search")

        val query = readInput("Search query: ") ?: return
        val minPrice = readInput("Minimum price (optional): ")?.toDoubleOrNull()
        val maxPrice = readInput("Maximum price (optional): ")?.toDoubleOrNull()

        println("Categories: ${ProductCategory.values().joinToString(", ") { it.displayName }}")
        val categoryInput = readInput("Category (optional): ")
        val category = ProductCategory.values().find {
            it.displayName.equals(categoryInput, ignoreCase = true)
        }

        val results = productService.searchProducts(query, minPrice, maxPrice, category)

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

    fun updateProductStock() {
        println("\nUpdate Product Stock")

        val productId = readInput("Product ID: ") ?: return
        val product = productService.findById(productId)

        if (product == null) {
            println("Product not found")
            return
        }

        println("Current product: ${product.name}")
        println("Current stock: ${product.stock}")

        val adjustment =
            readInput("Stock adjustment (+ to add, - to remove): ")?.toIntOrNull() ?: return

        productService.updateStock(productId, adjustment).onSuccess { updatedProduct ->
            println("Stock updated successfully!")
            println("New stock level: ${updatedProduct.stock}")
        }.onFailure {
            println("Error: ${it.message}")
        }

    }

    fun displayProductDetails() {
        println("\nProduct Details")

        val productId = readInput("Product ID: ") ?: return
        val product = productService.findById(productId)

        if (product == null) {
            println("Product not found")
            return
        }

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

    fun showProductAnalytics() {
        println("\nProduct Analytics")

        val report = productService.generateInventoryReport()

        println("\n" + "=".repeat(60))
        println("INVENTORY OVERVIEW")
        println("=".repeat(60))
        println("Total Products: ${report.totalProducts}")
        println("Total Value: ${report.totalValue.toInt()} VND")

        println("=".repeat(60))
    }

    // ============= CUSTOMER MANAGEMENT =============

    @RequiresApi(Build.VERSION_CODES.O)
    fun customerManagement() {
        while (true) {
            println("\n === CUSTOMER MANAGEMENT ===")
            println("1. Create New Customer")
            println("2. Find Customer")
            println("3. View Customer Type")
            println("0. Return to Main Menu")

            when (readInput("\nOption: ")?.toIntOrNull() ?: -1) {
                1 -> createNewCustomer()
                2 -> findCustomer()
                3 -> viewCustomerTypes()
                0 -> return
                else -> println("Invalid option")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewCustomer() {
        println("\nCreate New Customer")

        val name = readInput("Customer Name: ") ?: return

        var email: String
        do {
            email = readInput("Email: ") ?: return
            if (!email.isValidEmail()) {
                println("Invalid email format")
            }
        } while (!email.isValidEmail())

        val phone = readInput("Phone (optional): ")

        customerService.createCustomer(name, email, phone).onSuccess { customer ->
            println("Customer created successfully!")
            println("Customer ID: ${customer.id}")
        }.onFailure {
            println("Error: ${it.message}")
        }
    }

    fun findCustomer() {
        println("\nFind Customer")

        val id = readInput("Customer ID: ") ?: return
        val customer = customerService.findById(id)

        if (customer != null) {
            displayCustomerDetails(customer)
        } else {
            println("Customer not found")
        }
    }

    fun displayCustomerDetails(customer: Customer) {
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

    fun viewCustomerTypes() {
        println("\nCustomer Type")

        CustomerType.values().forEach { type ->
            val customers = customerService.getCustomersByType(type)
            println("\n${type.displayName} (${(type.discountRate * 100).toInt()}% discount)")
            println("  Minimum spending: ${type.minSpending.toInt()} VND")
            println("  Customers: ${customers.size}")
        }
    }

    // ============= ORDER MANAGEMENT =============

    @RequiresApi(Build.VERSION_CODES.O)
    fun orderManagement() {
        while (true) {
            println("\n=== ORDER MANAGEMENT ===")
            println("1. Create New Order")
            println("2. View All Orders")
            println("3. Find Order")
            println("4. Order Analytics")
            println("0. Return to Main Menu")

            when (readInput("\nOption: ")?.toIntOrNull() ?: -1) {
                1 -> createNewOrder()
                2 -> viewAllOrders()
                3 -> findOrder()
                4 -> showOrderAnalytics()
                0 -> return
                else -> println("Invalid option")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNewOrder() {
        println("\nCreate New Order")

        val customerId = readInput("Customer ID: ") ?: return

        orderService.createOrder(customerId).onSuccess { order ->
            println("Order created: ${order.id}")

            while (true) {
                val addItem = readInput("Add item to order? (y/n): ")
                if (addItem?.lowercase() != "y") break

                val productId = readInput("Product ID: ") ?: continue
                val quantity = readInput("Quantity: ")?.toIntOrNull() ?: continue

                orderService.addItemToOrder(order.id, productId, quantity)
            }

            val processOrder = readInput("Process order now? (y/n): ")
            if (processOrder?.lowercase() == "y") {
                orderService.processOrder(order.id).onSuccess { processedOrder ->
                    println("Order processed successfully!")
                    println("Total Amount: ${processedOrder.getTotalAmount().toInt()} VND")
                    currentRevenue += processedOrder.getTotalAmount()
                }.onFailure {
                    println("Error processing order: ${it.message}")
                }
            }
        }.onFailure {
            println("Error: ${it.message}")
        }
    }

    fun viewAllOrders() {
        println("\nAll Orders")

        val orders = orderService.findAll()

        if (orders.isEmpty()) {
            println("No orders found")
            return
        }

        println("\nFound ${orders.size} orders:")
        println("-".repeat(80))
        orders.forEach { order ->
            println(
                "${order.id} | ${order.customer.name} | ${order.status} | ${
                    order.getTotalAmount().toInt()
                } VND"
            )
        }
        println("-".repeat(80))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun findOrder() {
        println("\nFind Order")

        val orderId = readInput("Order ID: ") ?: return
        val order = orderService.findById(orderId)

        if (order != null) {
            displayOrderDetails(order)
        } else {
            println("Order not found")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun displayOrderDetails(order: Order) {
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

    fun showOrderAnalytics() {
        println("\nOrder Analytics")

        val orders = orderService.findAll()
        val totalRevenue = orderService.getTotalRevenue()

        println("\n" + "=".repeat(60))
        println("ORDER STATISTICS")
        println("=".repeat(60))
        println("Total Orders: ${orders.size}")
        println("Total Revenue: ${totalRevenue.toInt()} VND")

        println("=".repeat(60))
    }

    // ============= REPORTS AND ANALYTICS =============

    fun reportsAndAnalytics() {
        while (true) {
            println("\n=== REPORTS & ANALYTICS ===")
            println("1. Inventory Report")
            println("2. Sales Report")
            println("3. Revenue Summary")
            println("0. Return to Main Menu")

            when (readInput("\nOption: ")?.toIntOrNull() ?: -1) {
                1 -> showInventoryReport()
                2 -> showSalesReport()
                3 -> showRevenueSummary()
                0 -> return
                else -> println("Invalid option")
            }
        }
    }

    fun showInventoryReport() {
        println("\nInventory Report")
        val report = productService.generateInventoryReport()

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

    fun showSalesReport() {
        println("\nSales Report")
        val report = orderService.generateSalesReport()

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

    fun showRevenueSummary() {
        println("\nRevenue Summary")

        val totalRevenue = orderService.getTotalRevenue()
        val orders = orderService.findAll()

        println("\n" + "=".repeat(60))
        println("REVENUE OVERVIEW")
        println("=".repeat(60))
        println("System Revenue: ${currentRevenue.toInt()} VND")
        println("Total Delivered Revenue: ${totalRevenue.toInt()} VND")
        println("Total Orders: ${orders.size}")

        val statusBreakdown = orders.groupingBy { it.status }.eachCount()
        println("\nBY STATUS")
        println("-".repeat(40))
        statusBreakdown.forEach { (status, count) ->
            println("${status}: $count orders")
        }

        println("=".repeat(60))
    }

    // ============= UTILITY METHODS =============
    fun readInput(prompt: String): String? {
        print(prompt)
        return readlnOrNull()?.takeIf { it.isNotBlank() }
    }
}
