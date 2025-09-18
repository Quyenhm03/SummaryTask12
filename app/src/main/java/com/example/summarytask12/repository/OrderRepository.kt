package com.example.summarytask12.repository

import com.example.summarytask12.model.Customer
import com.example.summarytask12.model.Order
import com.example.summarytask12.util.DatabaseConnect

class OrderRepository(private val inventory: InventoryRepository) {
    private val orders: MutableSet<Order> = mutableSetOf()
    private val customers: MutableMap<String, Customer> = mutableMapOf()
    private var orderCounter: Int = 0

    init {
        if (!DatabaseConnect.isConnected()) {
            throw IllegalStateException("Database not connected")
        }
    }

    fun addCustomer(customer: Customer) {
        customers[customer.id] = customer
        println("Added: ${customer.name}")
        DatabaseConnect.query("INSERT INTO customers(id, name, email) VALUES (${customer.id}, ${customer.name}, ${customer.email})")
    }

    fun createOrder(customerId: String, productIds: List<String>) : Order? {
        val customer = customers[customerId] ?: run {
            println("Customer $customerId not found")
            return null
        }

        orderCounter++

        val order = Order("ORD-$orderCounter", customer)
        DatabaseConnect.query("INSERT INTO orders(id, customer_id) VALUES ('${order.id}', '${customer.id}')")
        for (pid in productIds) {
            val product = inventory.findById(pid) ?: continue
            if (order.addItem(product)) {
                println("Item $pid added to order")
                DatabaseConnect.query("INSERT INTO order_items(order_id, product_id) VALUES ('${order.id}', '$pid')")
            }
        }

        if (order.items.isNotEmpty()) {
            orders.add(order)
            customer.addToHistory(order)
            DatabaseConnect.query("INSERT INTO customer_orders(customer_id, order_id) VALUES ('${customer.id}', '${order.id}')")
            return order
        }
        return null
    }

    fun getOrderCount(): Int = orderCounter

    fun getTotalRevenue() = orders.sumOf { it.totalAmount }

    fun processPendingOrders() {
        val pending = orders.filter {
            it.orderDate == null
        }
        pending.reversed().forEach { order ->
            println("Pending: $order.is - Sending reminder")
            order.customer.sendNotification("Update your order ${order.id}")
        }
    }
}