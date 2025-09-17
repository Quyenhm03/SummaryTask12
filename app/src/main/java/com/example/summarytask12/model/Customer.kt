package com.example.summarytask12.model

import com.example.summarytask12.extension.isValidEmail

data class Customer (
    private val id: String,
    private val name: String,
    private var email: String? = null,
    private var address: String? = null,
    private var phone: String? = null
) {
    private val purchaseHistory = mutableListOf<Order>()
    constructor(id: String, name: String, email: String?) : this(id, name, email, null, null)

    fun getId(): String = id
    fun getName(): String = name
    fun getEmail(): String? = email
    fun getAddress(): String? = address
    fun getPhone(): String? = phone

    fun setEmail(newEmail: String?) {
        if (newEmail == null || newEmail.isValidEmail()) {
            email = newEmail
        }
        else {
            println("Invalid email format")
        }
    }

    fun setAddress(newAddress: String?) {
        address = newAddress
    }

    fun setPhone(newPhone: String?) {
        phone = newPhone
    }

    fun addToHistory(order: Order) {
        purchaseHistory.add(order)
    }

    fun getTotalSpent(): Double = purchaseHistory.sumOf { it.totalAmount }

    fun getContactInfo(): String {
        val contact = email ?: phone ?: "No contact"
        val isValidEmail = email?.contains("@") ?: false
        return "$contact${if (isValidEmail) " (verified)" else ""}"
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