//package com.example.summarytask12
//
//import com.example.summarytask12.extension.getCustomerSegment
//import com.example.summarytask12.extension.isValidEmail
//import com.example.summarytask12.model.customer.Customer
//import com.example.summarytask12.model.product.Clothing
//import com.example.summarytask12.model.product.Electronic
//import com.example.summarytask12.repository.OrderRepository
//import com.example.summarytask12.model.product.ClothingSize
//import com.example.summarytask12.util.DatabaseConnect
//import kotlin.system.exitProcess
//
//fun main() {
//    println("=== Welcome Store ===")
//    DatabaseConnect.connect()
//
//    val inventory = InventoryRepository()
//    val orderRepo = OrderRepository(inventory)
//    var currentRevenue = 0.0
//
//    initData(inventory, orderRepo)
//
//    while (true) {
//        showMainMenu()
//        val choice = readln().toIntOrNull() ?: 0
//        when (choice) {
//            1 -> productManagement(inventory)
//            2 -> customerManagement(orderRepo)
//            3 -> orderManagement(orderRepo) { revenue -> currentRevenue += revenue }
//            4 -> reportsAndStatistics(inventory, orderRepo, currentRevenue)
//            5 -> {
//                println("Shutting down system...")
//                DatabaseConnect.disconnect()
//                println("Total revenue: $currentRevenue VND")
//                exitProcess(0)
//            }
//
//            else -> println("Invalid option, please try again.")
//        }
//        println("\nPress Enter to continue...")
//        readlnOrNull()
//    }
//}
//
//fun showMainMenu() {
//    println("\n" + "=".repeat(50))
//    println("STORE MANAGEMENT SYSTEM")
//    println("=".repeat(50))
//    println("1. Product Management")
//    println("2. Customer Management")
//    println("3. Order Management")
//    println("4. Reports and Statistics")
//    println("5. Exit")
//    print("\nChoose an option (1-5): ")
//}
//
//fun initData(inventory: InventoryRepository, orderRepo: OrderRepository) {
//    println("Init data ....")
//
//    val laptop = Electronic("LAP1", "Dell", 13500000.0, 2)
//    laptop.updateStock(10)
//    inventory.addProduct(laptop)
//
//    val shirt = Clothing("SH1", "Shirt", 150000.0, ClothingSize.M)
//    shirt.updateStock(25)
//    inventory.addProduct(shirt)
//
//    val trousers = Clothing("TR1", "Trousers", 250000.0, ClothingSize.S)
//    trousers.updateStock(10)
//    inventory.addProduct(trousers)
//
//    val customer = Customer.create("Nguyen Van A", "nguyenvana@gmail.com").apply {
//        address = "33 Trang Thi Street"
//        phone = "0123456789"
//    }
//    orderRepo.addCustomer(customer)
//
//    println("Add data successfully!")
//}
//
//fun productManagement(inventory: InventoryRepository) {
//    while (true) {
//        println("\n=== PRODUCT MANAGEMENT ===")
//        println("1. Add electronic product")
//        println("2. Add clothing product")
//        println("3. Search products")
//        println("4. Update product stock")
//        println("5. Display product details")
//        println("0. Return to main menu")
//        print("\nOption: ")
//
//        when (readlnOrNull()?.toIntOrNull() ?: -1) {
//            1 -> addElectronic(inventory)
//            2 -> addClothing(inventory)
//            3 -> searchProducts(inventory)
//            4 -> updateStock(inventory)
//            5 -> displayProductDetails(inventory)
//            0 -> return
//            else -> println("Invalid option")
//        }
//    }
//}
//
//fun addElectronic(inventory: InventoryRepository) {
//    println("\nAdd electronic product")
//
//    print("Product ID: ")
//    val id = readlnOrNull() ?: return
//
//    print("Product name: ")
//    val name = readlnOrNull() ?: return
//
//    print("Price: ")
//    val price = readlnOrNull()?.toDoubleOrNull() ?: return
//
//    print("Warranty years (optional): ")
//    val warranty = readlnOrNull()?.toIntOrNull()
//
//    print("Initial stock: ")
//    val stock = readlnOrNull()?.toIntOrNull() ?: 0
//
//    try {
//        val product = if (warranty != null) {
//            Electronic(id, name, price, warranty)
//        } else {
//            Electronic(id, name, price)
//        }
//        product.updateStock(stock)
//        inventory.addProduct(product)
//        println("Electronic product added successfully!")
//    } catch (e: Exception) {
//        println("Error: ${e.message}")
//    }
//}
//
//fun addClothing(inventory: InventoryRepository) {
//    println("\nAdd clothing product")
//
//    print("Product ID: ")
//    val id = readlnOrNull() ?: return
//
//    print("Product name: ")
//    val name = readlnOrNull() ?: return
//
//    print("Price: ")
//    val price = readlnOrNull()?.toDoubleOrNull() ?: return
//
//    print("Size (S/M/L/XL): ")
//    val size = readlnOrNull() ?: return
//
//    print("Initial stock: ")
//    val stock = readlnOrNull()?.toIntOrNull() ?: 0
//
//    try {
//        val product = Clothing(id, name, price, size)
//        product.updateStock(stock)
//        inventory.addProduct(product)
//        println("Clothing item added successfully!")
//    } catch (e: Exception) {
//        println("Error: ${e.message}")
//    }
//}
//
//fun searchProducts(inventory: InventoryRepository) {
//    println("\nProduct Search")
//
//    print("Name product: ")
//    val query = readlnOrNull() ?: return
//
//    print("Minimum price (optional): ")
//    val minPrice = readlnOrNull()?.toDoubleOrNull() ?: 0.0
//
//    print("Category (Electronics/Clothing): ")
//    val category = readlnOrNull()?.takeIf { it.isNotBlank() }
//
//    val results = inventory.searchProducts(query, minPrice, category)
//    if (results.isEmpty()) {
//        println("No products found")
//    } else {
//        println("Products found:")
//        results.forEach {
//            println("   $it")
//        }
//    }
//}
//
//fun updateStock(inventory: InventoryRepository) {
//    print("Product ID: ")
//    val id = readlnOrNull() ?: return
//
//    print("Add stock: ")
//    val addStock = readlnOrNull()?.toIntOrNull() ?: return
//
//    inventory.updateStock(id, addStock)
//    println("Stock updated!")
//}
//
//fun displayProductDetails(inventory: InventoryRepository) {
//    print("Product ID: ")
//    val id = readlnOrNull() ?: return
//
//    val product = inventory.findById(id)
//    val description = product?.getDescription()
//    if (description != null) {
//        println("Details: $description")
//    } else {
//        println("Product not found")
//    }
//}
//
//fun customerManagement(orderRepo: OrderRepository) {
//    while (true) {
//        println("\n=== CUSTOMER MANAGEMENT ===")
//        println("1. Create new customer")
//        println("2. Update customer info")
//        println("3. Display customer segment by age")
//        println("4. Test email validation")
//        println("5. Send customer notification")
//        println("6. Display list customer")
//        println("0. Return to main menu")
//        print("\nOption: ")
//
//        when (readlnOrNull()?.toIntOrNull() ?: -1) {
//            1 -> createCustomer(orderRepo)
//            2 -> updateCustomerInfo()
//            3 -> displayCustomerSegment()
//            4 -> testEmailValidation()
//            5 -> sendNotification(orderRepo)
//            6 -> displayListCustomer(orderRepo)
//            0 -> return
//            else -> println("Invalid option")
//        }
//    }
//}
//
//fun createCustomer(orderRepo: OrderRepository) {
//    print("Customer name: ")
//    val name = readlnOrNull() ?: return
//
//    print("Email: ")
//    var firstInput = true
//    var email: String
//    do {
//        email = readlnOrNull()?.takeIf { it.isNotBlank() }.toString()
//        if (firstInput) {
//            firstInput = false
//        } else {
//            print("Please enter valid email: ")
//        }
//    } while (!email.isValidEmail())
//
//    val customer = Customer.create(name, email)
//    orderRepo.addCustomer(customer)
//    println("Customer created with ID: ${customer.id}")
//}
//
//fun updateCustomerInfo() {
//    val customer = Customer.create("Test User", "test@example.com")
//    customer.apply {
//        address = "New address"
//        phone = "0987654321"
//        println("Information updated for $name")
//        println("Contact: ${getContactInfo()}")
//    }
//}
//
//fun displayCustomerSegment() {
//    print("Customer age: ")
//    val age = readlnOrNull()?.toIntOrNull() ?: return
//
//    val testCustomer = Customer.create("Test", "test@gmail.com")
//    val segment = testCustomer.getCustomerSegment(age)
//    println("Customer segment: $segment")
//}
//
//fun testEmailValidation() {
//    print("Email to test: ")
//    val email = readlnOrNull()
//    val isValid = email.isValidEmail()
//    println("Valid email: $isValid")
//}
//
//fun sendNotification(orderRepo: OrderRepository) {
//    print("CustomerID: ")
//    val id = readlnOrNull() ?: return
//
//    val customer = orderRepo.getCustomerByID(id)
//    if (customer != null) {
//        customer.sendNotification("Notification test")
//    } else {
//        println("Customer $id not found!")
//    }
//}
//
//fun displayListCustomer(orderRepo: OrderRepository) {
//    println("All customer of store:")
//    val listCustomer = orderRepo.getAllCustomer()
//    listCustomer.forEach {
//        println("   $it")
//    }
//}
//
//fun orderManagement(orderRepo: OrderRepository, updateRevenue: (Double) -> Unit) {
//    while (true) {
//        println("\n=== ORDER MANAGEMENT ===")
//        println("1. Create new order")
//        println("2. Display all orders")
//        println("3. Display recent orders")
//        println("4. Calculate total revenue")
//        println("0. Return to main menu")
//        print("\nOption: ")
//
//        when (readlnOrNull()?.toIntOrNull() ?: -1) {
//            1 -> createOrder(orderRepo, updateRevenue)
//            2 -> orderRepo.printOrders()
//            3 -> displayRecentOrders(orderRepo)
//            4 -> println("Total revenue: ${orderRepo.getTotalRevenue().toInt()} VND")
//            0 -> return
//            else -> println("Invalid option")
//        }
//    }
//}
//
//fun createOrder(orderRepo: OrderRepository, updateRevenue: (Double) -> Unit) {
//    print("Customer ID: ")
//    val customerId = readlnOrNull() ?: return
//    print("Product IDs (comma separated): ")
//    val productIds = readlnOrNull()?.split(",")?.map { it.trim() } ?: return
//
//    val order = orderRepo.createOrder(customerId, productIds)
//    if (order != null) {
//        println("Order created: ${order.id}")
//        println("Total: ${order.totalAmount} VND")
//        updateRevenue(order.totalAmount)
//    } else {
//        println("Failed to create order")
//    }
//}
//
//fun displayRecentOrders(orderRepo: OrderRepository) {
//    print("Number of recent orders: ")
//    val count = readlnOrNull()?.toIntOrNull() ?: 5
//
//    val recentOrders = orderRepo.getRecentOrders(count)
//    println("Recent orders:")
//    recentOrders.forEach { println("    $it") }
//}
//
//fun reportsAndStatistics(
//    inventory: InventoryRepository,
//    orderRepo: OrderRepository,
//    currentRevenue: Double
//) {
//    while (true) {
//        println("\n=== REPORTS AND STATISTICS ===")
//        println("1. Total inventory value")
//        println("2. Order statistics")
//        println("3. Current system revenue")
//        println("0. Return to main menu")
//        print("\nOption: ")
//
//        when (readlnOrNull()?.toIntOrNull() ?: -1) {
//            1 -> displayInventoryValue(inventory)
//            2 -> displayOrderStatistics(orderRepo)
//            3 -> println("Current system revenue: ${currentRevenue.toInt()} VND")
//            0 -> return
//            else -> println("Invalid option")
//        }
//    }
//}
//
//fun displayInventoryValue(inventory: InventoryRepository) {
//    val totalValue = inventory.getTotalInventoryValue()
//    println("Total inventory value: ${totalValue.toInt()} VND")
//}
//
//fun displayOrderStatistics(orderRepo: OrderRepository) {
//    println("Order statistics:")
//    println("   Total count: ${orderRepo.getOrderCount()}")
//    println("   Total revenue: ${orderRepo.getTotalRevenue().toInt()} VND")
//}
//
