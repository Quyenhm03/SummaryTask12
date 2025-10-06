package com.example.summarytask12.model.customer

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.extension.isValidEmail
import com.example.summarytask12.model.discount.DiscountContext
import com.example.summarytask12.model.order.Order
import com.example.summarytask12.util.Reportable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Customer(
    val id: String,
    val name: String,
    var email: String,
    var phone: String? = null,
    var address: Address? = null,
) : Reportable {
    val orderHistory = mutableListOf<Order>()
    var customerType = CustomerType.BRONZE
    var totalSpent = 0.0

    companion object {
        var customerCounter = 0

        fun create(name: String, email: String, phone: String?): Result<Customer> {
            return if (email.isValidEmail()) {
                customerCounter++
                Result.success(
                    Customer(
                        "CUST-${customerCounter.toString().padStart(5, '0')}",
                        name,
                        email,
                        phone
                    )
                )
            } else {
                Result.failure(IllegalArgumentException("Invalid email address"))
            }
        }
    }

    fun updateContactInfo(email: String, phone: String, address: Address): Result<Unit> {
        return if (email.isValidEmail()) {
            this.email = email
            this.phone = phone
            this.address = address
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid email address"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addOrder(order: Order) {
        orderHistory.add(order)
        totalSpent += order.getTotalAmount()

        updateType()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateType() {
        val newType = when {
            totalSpent >= CustomerType.PLATINUM.minSpending -> {
                CustomerType.PLATINUM
            }

            totalSpent >= CustomerType.GOLD.minSpending -> {
                CustomerType.GOLD
            }

            totalSpent >= CustomerType.SILVER.minSpending -> {
                CustomerType.SILVER
            }

            else -> {
                CustomerType.BRONZE
            }
        }

        if (newType != customerType) {
            val oldType = customerType
            customerType = newType
            sendNotification("Congratulations! You've been upgraded from ${oldType.displayName} to ${newType.displayName}!")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendNotification(
        message: String, format: (String, String, String) -> String = { id, name, mes ->
            val timestamp =
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            "[$timestamp] for $name ($id): $message"
        }
    ) {
        println(format(id, name, message))
    }

    private fun getAverageOrderValue(): Double {
        var averageOrderValue = 0.0

        if (orderHistory.isNotEmpty()) {
            averageOrderValue = totalSpent / orderHistory.size
        } else {
            // do nothing
        }

        return averageOrderValue
    }

    override fun generateReport(): Map<String, Any> {
        return mapOf(
            "customerId" to id,
            "name" to name,
            "customerType" to customerType.displayName,
            "totalSpent" to totalSpent,
            "totalOrders" to orderHistory.size,
            "averageOrderValue" to getAverageOrderValue()
        )
    }

    override fun toString(): String {
        return "Customer(id='$id', name='$name', customerType=${customerType.displayName}, totalSpent=${totalSpent.toInt()})"
    }
}