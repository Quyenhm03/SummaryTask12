package com.example.summarytask12.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.model.order.Order
import com.example.summarytask12.model.order.OrderStatus
import com.example.summarytask12.repository.OrderRepository

class OrderService(
    val orderRepository: OrderRepository,
    val productService: ProductService,
    val customerService: CustomerService
) {

    fun createOrder(customerId: String): Result<Order> {
        return customerService.findById(customerId)?.let { customer ->
            val orderId = orderRepository.generateOrderId()
            val order = Order(orderId, customer)
            orderRepository.save(order)
        } ?: Result.failure(NoSuchElementException("Customer not found"))
    }

    fun addItemToOrder(orderId: String, productId: String, quantity: Int) {
        val order = orderRepository.findById(orderId)
        val product = productService.findById(productId)

        if (order != null) {
            if (product != null) {
                order.addItem(product, quantity)
            }
        }

        if (order != null) {
            orderRepository.update(order)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun processOrder(orderId: String): Result<Order> {
        val order = orderRepository.findById(orderId) ?: return Result.failure(
            NoSuchElementException("Order not found")
        )

        for (item in order.items) {
            if (item.product.stock < item.quantity) {
                return Result.failure(IllegalStateException("Insufficient stock for ${item.product.name}"))
            }
        }

        order.items.forEach { item ->
            productService.updateStock(item.product.id, -item.quantity)
        }

        order.status = OrderStatus.CONFIRMED
        order.customer.addOrder(order)

        return orderRepository.update(order)
    }

    fun findById(id: String): Order? = orderRepository.findById(id)

    fun findAll(): List<Order> = orderRepository.findAll()

    fun getOrdersByCustomer(customerId: String): List<Order> {
        return orderRepository.findByCustomer(customerId)
    }

    fun getTotalRevenue(): Double = orderRepository.getTotalRevenue()

    fun generateSalesReport(): SalesReport {
        val orders = findAll()
        val totalRevenue = findAll().sumOf { it.getTotalAmount() }

        return SalesReport(
            period = "Current Period",
            totalRevenue = totalRevenue,
            totalOrders = orders.size,
        )
    }
}