package com.example.summarytask12.repository

import com.example.summarytask12.model.order.Order
import com.example.summarytask12.model.order.OrderStatus
import com.example.summarytask12.util.DatabaseConnect

class OrderRepository {
    val orders: MutableMap<String, Order> = mutableMapOf()
    var orderCounter: Int = 0

    fun save(order: Order): Result<Order> {
        return try {
            orders[order.id] = order
            val query =
                "INSERT INTO orders (id, customer_id, status, total_amount) VALUES ('${order.id}', '${order.customer.id}', '${order.status.name}', ${order.getTotalAmount()})"
            DatabaseConnect.query(query)
            println("Order saved: ${order.id}")
            Result.success(order)
        } catch (e: Exception) {
            Result.failure(e)
        }

    }

    fun findById(id: String): Order? = orders[id]

    fun findAll(): List<Order> = orders.values.toList()

    fun update(order: Order): Result<Order> {
        return if (orders.containsKey(order.id)) {
            orders[order.id] = order
            val query = "UPDATE orders SET status='${order.status.name}' WHERE id='${order.id}'"
            DatabaseConnect.query(query)
            Result.success(order)
        } else {
            Result.failure(NoSuchElementException("Order not found"))
        }
    }

    fun generateOrderId(): String {
        orderCounter++
        return "ORD-${orderCounter.toString().padStart(5, '0')}"
    }

    fun findByCustomer(customerId: String): List<Order> {
        return orders.values.filter { it.customer.id == customerId }
    }

    fun findByStatus(status: OrderStatus): List<Order> {
        return orders.values.filter { it.status == status }
    }

    fun getTotalRevenue(): Double {
        return orders.values.sumOf { it.getTotalAmount() }
    }
}