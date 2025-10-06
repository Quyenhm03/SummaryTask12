package com.example.summarytask12.model.order

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.model.customer.Address
import com.example.summarytask12.model.customer.Customer
import com.example.summarytask12.model.discount.DiscountContext
import com.example.summarytask12.model.product.Product
import com.example.summarytask12.util.Reportable
import java.time.LocalDateTime

class Order(
    val id: String,
    val customer: Customer,
) : Reportable {
    var items: MutableList<OrderItem> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.O)
    var orderDate: LocalDateTime = LocalDateTime.now()
    var shippingAddress: Address? = null
    var status: OrderStatus = OrderStatus.PENDING

    fun addItem(product: Product, quantity: Int = 1): Result<Unit> {
        return if (product.stock >= quantity && quantity > 0) {
            val existingItem = items.find { it.product.id == product.id }
            if (existingItem != null) {
                val newQuantity = existingItem.quantity + quantity
                if (product.stock >= newQuantity) {
                    existingItem.quantity = newQuantity
                    Result.success(Unit)
                } else {
                    Result.failure(IllegalStateException("Stock not enough"))
                }
            } else {
                val newItem = OrderItem(product, quantity, product.price)
                items.add(newItem)
                Result.success(Unit)
            }
        } else {
            Result.failure(IllegalStateException("Stock not enough or invalid quantity"))
        }
    }

    fun getSubtotal(): Double = items.sumOf { it.getSubtotal() }

    fun getTotalDiscount(): Double =
        items.sumOf { it.getDiscount() } + customerDiscount(getSubtotal())

    fun customerDiscount(amount: Double): Double {
        val context = DiscountContext(
            customerType = customer.customerType,
            isFirstPurchase = customer.orderHistory.isEmpty(),
            orderTotal = getSubtotal()
        )

        var totalDiscount = amount * context.customerType.discountRate

        if (context.isFirstPurchase) {
            totalDiscount += amount * 0.02
        }

        return minOf(totalDiscount, amount * 0.7)
    }

    fun getTotalAmount() = getSubtotal() - getTotalDiscount()

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStatus(newStatus: OrderStatus) {
        val oldStatus = status
        status = newStatus

        customer.sendNotification(
            "Your order $id status changed from ${oldStatus} to ${newStatus}",
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun generateReport(): Map<String, Any> {
        return mapOf(
            "orderId" to id,
            "customerId" to customer.id,
            "customerName" to customer.name,
            "status" to status,
            "orderDate" to orderDate,
            "itemCount" to items.size,
            "subtotal" to getSubtotal(),
            "totalDiscount" to getTotalDiscount(),
            "totalAmount" to getTotalAmount()
        )
    }

    override fun toString(): String {
        return "Order(id='$id', customer='${customer.name}', items=${items.size}, total=${getTotalAmount().toInt()}, status=${status})"
    }
}