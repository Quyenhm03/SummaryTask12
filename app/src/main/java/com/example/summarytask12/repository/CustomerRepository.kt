package com.example.summarytask12.repository

import com.example.summarytask12.model.customer.Customer
import com.example.summarytask12.model.customer.CustomerType
import com.example.summarytask12.util.DatabaseConnect

class CustomerRepository {
    val customers: MutableMap<String, Customer> = mutableMapOf()

    fun save(customer: Customer): Result<Customer> {
        return try {
            customers[customer.id] = customer
            val query =
                "INSERT INTO customers (id, name, email, type, total_spent) VALUES ('${customer.id}', '${customer.name}', '${customer.email}', '${customer.customerType.name}', ${customer.totalSpent})"
            DatabaseConnect.query(query)
            println("Customer saved: ${customer.id}")
            Result.success(customer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun findById(id: String): Customer? = customers[id]

    fun findAll(): List<Customer> = customers.values.toList()

    fun update(customer: Customer): Result<Customer> {
        return if (customers.containsKey(customer.id)) {
            customers[customer.id] = customer
            val query =
                "UPDATE customers SET name='${customer.name}', tier='${customer.customerType.name}', total_spent=${customer.totalSpent} WHERE id='${customer.id}'"
            DatabaseConnect.query(query)
            Result.success(customer)
        } else {
            Result.failure(NoSuchElementException("Customer not found"))
        }
    }

    fun findByEmail(email: String): Customer? {
        return customers.values.find { it.email == email }
    }

    fun findByType(type: CustomerType): List<Customer> {
        return customers.values.filter { it.customerType == type }
    }
}