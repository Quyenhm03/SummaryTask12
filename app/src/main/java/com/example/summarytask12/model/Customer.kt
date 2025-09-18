package com.example.summarytask12.model

import com.example.summarytask12.extension.isValidEmail

data class Customer (
    val id: String,
    val name: String,
    var email: String? = null,
    var address: String? = null,
    var phone: String? = null
) {
    private val purchaseHistory = mutableListOf<Order>()
    constructor(id: String, name: String, email: String?) : this(id, name, email, null, null)

    companion object {
        fun generateId() : String = "CUST${System.currentTimeMillis() % 10000}"
        fun create(name: String, email: String? = null): Customer = Customer(generateId(), name, email)
    }

    fun addToHistory(order: Order) {
        purchaseHistory.add(order)
    }

    val totalSpent: Double
        get() = purchaseHistory.sumOf { it.totalAmount }

    fun getContactInfo(): String {
        val contact = email ?: phone ?: "No contact"
        val isValidEmail = email?.let { it.isValidEmail() } ?: false
        return "$contact${if (isValidEmail) " (verified)" else ""}"
    }

    fun getFullInfo(): String {
        val addr = address ?: "Address not provided"
        return "Customer: $name, Contact: ${getContactInfo()}, Address: ${addr.uppercase()}, Total Spent: $${totalSpent}"
    }

    fun sendNotification(msg: String) {
        email?.let { em ->
            if (em.contains("@")) {
                println("Email to $em: $msg")
            }
            else {
                println("Invalid email")
            }
        } ?: phone?.let { ph ->
            println("SMS to $ph: $msg")
        } ?: println("No contact for $msg")
    }

}