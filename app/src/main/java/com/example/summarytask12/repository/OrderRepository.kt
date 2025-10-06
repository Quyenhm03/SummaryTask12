package com.example.summarytask12.repository

import com.example.summarytask12.model.order.Order
import com.example.summarytask12.model.order.OrderItem
import com.example.summarytask12.model.order.OrderStatus
import com.example.summarytask12.util.DatabaseConnect

class OrderRepository {
    val orders: MutableMap<String, Order> = mutableMapOf()
    val orderItems: MutableMap<String, MutableList<OrderItem>> = mutableMapOf()
    var orderCounter: Int = 0

    fun save(order: Order): Result<Order> {
        return try {
            orders[order.id] = order
            orderItems[order.id] = order.items

            val orderQuery = "INSERT INTO orders (id, customerId, status, totalAmount) " +
                    "VALUES ('${order.id}', '${order.customer.id}', '${order.status.name}', ${order.getTotalAmount()})"
            DatabaseConnect.query(orderQuery)
            println("Order saved: ${order.id}")

            order.items.forEach { item ->
                val itemQuery =
                    "INSERT INTO order_items (orderId, productId, quantity, subtotal) " +
                            "VALUES ('${order.id}', '${item.product.id}', ${item.quantity}, ${item.getSubtotal()})"
                DatabaseConnect.query(itemQuery)
                println("Added item: ${item.product.name} x${item.quantity}")
            }

            Result.success(order)
        } catch (e: Exception) {
            println("Error saving order: ${e.message}")
            Result.failure(e)
        }
    }

    fun findById(id: String): Order? {
        val query = "SELECT * FROM orders WHERE id = '$id'"
        DatabaseConnect.query(query)

        val order = orders[id] ?: return null
        val items = orderItems[id] ?: mutableListOf()
        order.items = items
        return order
    }

    fun findAll(): List<Order> {
        val query = "SELECT * FROM orders"
        DatabaseConnect.query(query)
        return orders.values.toList()
    }

    fun update(order: Order): Result<Order> {
        return if (orders.containsKey(order.id)) {
            orders[order.id] = order
            orderItems[order.id] = order.items

            val query =
                "UPDATE orders SET status='${order.status.name}', total_amount=${order.getTotalAmount()} WHERE id='${order.id}'"
            DatabaseConnect.query(query)

            DatabaseConnect.query("DELETE FROM order_items WHERE orderId='${order.id}'")
            order.items.forEach { item ->
                val itemQuery = "INSERT INTO orderItems (orderId, productId, quantity, subtotal) " +
                        "VALUES ('${order.id}', '${item.product.id}', ${item.quantity}, ${item.getSubtotal()})"
                DatabaseConnect.query(itemQuery)
            }

            println("Order updated: ${order.id}")
            Result.success(order)
        } else {
            Result.failure(NoSuchElementException("Order not found"))
        }
    }

    fun updateStatus(order: Order, newStatus: OrderStatus): Result<Order> {
        return if (orders.containsKey(order.id)) {
            orders[order.id] = order

            val query = "UPDATE orders SET status='$newStatus' WHERE id='${order.id}'"
            DatabaseConnect.query(query)

            println("Order updated: ${order.id}")
            Result.success(order)
        } else {
            Result.failure(NoSuchElementException("Order not found"))
        }
    }

    fun deleteOrder(id: String): Result<Unit> {
        return if (orders.containsKey(id)) {
            orders.remove(id)
            orderItems.remove(id)

            val deleteOrderQuery = "DELETE FROM orders WHERE id='$id'"
            val deleteItemsQuery = "DELETE FROM order_items WHERE orderId='$id'"
            DatabaseConnect.query(deleteOrderQuery)
            DatabaseConnect.query(deleteItemsQuery)

            println("Order deleted: $id")
            Result.success(Unit)
        } else {
            Result.failure(NoSuchElementException("Order not found"))
        }
    }

    fun generateOrderId(): String {
        orderCounter++
        return "ORD-${orderCounter.toString().padStart(5, '0')}"
    }

    fun getTotalRevenue(): Double {
        val query = "SELECT SUM(totalAmount) AS totalRevenue FROM orders"
        DatabaseConnect.query(query)
        return orders.values.sumOf { it.getTotalAmount() }
    }

    fun findByCustomer(customerId: String): List<Order> {
        val query = "SELECT * FROM orders WHERE customerId = '$customerId'"
        DatabaseConnect.query(query)
        return orders.values.filter { it.customer.id == customerId }
    }

    fun findByStatus(status: OrderStatus): List<Order> {
        val query = "SELECT * FROM orders WHERE status = '${status.name}'"
        DatabaseConnect.query(query)
        return orders.values.filter { it.status == status }
    }
}