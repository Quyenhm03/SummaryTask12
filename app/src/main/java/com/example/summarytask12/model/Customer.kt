package com.example.summarytask12.model

import com.example.summarytask12.extension.isValidEmail

data class Customer (
    val id: String,
    val name: String,
    var email: String,
    var address: String? = null,
    var phone: String? = null
) {
    private val purchaseHistory = mutableListOf<Order>()
    constructor(id: String, name: String, email: String) : this(id, name, email, null, null)

    companion object {
        private fun generateId() : String = "CUST${System.currentTimeMillis() % 10000}"
        fun create(name: String, email: String): Customer {
            if (email.isValidEmail()) {
                return Customer(generateId(), name, email)
            } else {
                throw IllegalArgumentException("Invalid email: $email")
            }
        }
    }

    fun addToHistory(order: Order) {
        purchaseHistory.add(order)
    }

    private val totalSpent: Double
        get() = purchaseHistory.sumOf { it.totalAmount }

    fun getContactInfo(): String {
        val isValidEmail = email.let { it.isValidEmail() } ?: false
        return "$email${if (isValidEmail) " (verified)" else ""}"
    }

    fun getFullInfo(): String {
        val addr = address ?: "Address not provided"
        return "Customer: $name, Contact: ${getContactInfo()}, Address: ${addr.uppercase()}, Total Spent: $${totalSpent}"
    }

    fun sendNotification(msg: String) {
        println("Email to $email: $msg")
    }

}