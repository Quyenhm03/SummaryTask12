package com.example.summarytask12.repository

import com.example.summarytask12.model.customer.Customer
import com.example.summarytask12.model.customer.CustomerType
import com.example.summarytask12.util.DatabaseConnect

class CustomerRepository {
    val customers: MutableMap<String, Customer> = mutableMapOf()

    fun save(customer: Customer): Result<Customer> {
        return try {
            customers[customer.id] = customer
            val queryCustomer =
                "INSERT INTO customers (id, name, email, phone, type, total_spent) " +
                        "VALUES ('${customer.id}', '${customer.name}', '${customer.email}' " +
                        ", '${customer.phone ?: ""}', '${customer.customerType.name}', '${customer.totalSpent}')"
            DatabaseConnect.query(queryCustomer)

            customer.address?.let { addr ->
                val queryAddress = "INSERT INTO address (customerId, street, city, country) " +
                        "VALUES ('${customer.id}', '${addr.street}', '${addr.city}', '${addr.country}')"
                DatabaseConnect.query(queryAddress)
            }

            println("Customer saved: ${customer.id}")
            Result.success(customer)
        } catch (e: Exception) {
            println("Error saving customer: ${e.message}")
            Result.failure(e)
        }
    }

    fun findById(id: String): Customer? {
        val query = "SELECT * FROM customers WHERE id = '$id'"
        DatabaseConnect.query(query)
        return customers[id]
    }

    fun findAll(): List<Customer> {
        val query = "SELECT * FROM customers"
        DatabaseConnect.query(query)
        return customers.values.toList()
    }

    fun update(customer: Customer): Result<Customer> {
        return if (customers.containsKey(customer.id)) {
            customers[customer.id] = customer
            val queryCustomer =
                "UPDATE customers SET name='${customer.name}', email='${customer.email}', " +
                        "phone='${customer.phone ?: ""}', type='${customer.customerType.name}', " +
                        "total_spent=${customer.totalSpent} WHERE id='${customer.id}'"
            DatabaseConnect.query(queryCustomer)

            customer.address?.let { addr ->
                val queryAddress =
                    "UPDATE address SET street='${addr.street}', city='${addr.city}', " +
                            "country='${addr.country}' WHERE id='${customer.id}'"
                DatabaseConnect.query(queryAddress)
            }

            println("Customer updated: ${customer.id}")
            Result.success(customer)
        } else {
            println("Customer not found: ${customer.id}")
            Result.failure(NoSuchElementException("Customer not found"))
        }
    }

    fun deleteById(id: String): Result<Unit> {
        return if (customers.containsKey(id)) {
            customers.remove(id)
            val query = "DELETE FROM customers WHERE id = '$id'"
            DatabaseConnect.query(query)
            Result.success(Unit)
        } else {
            println("Customer not found: $id")
            Result.failure(NoSuchElementException("Customer not found"))
        }
    }

    fun findByEmail(email: String): Customer? {
        val query = "SELECT * FROM customers WHERE email = '$email'"
        DatabaseConnect.query(query)
        return customers.values.find { it.email == email }
    }

    fun findByType(type: CustomerType): List<Customer> {
        val query = "SELECT * FROM customers WHERE type = '${type.name}'"
        DatabaseConnect.query(query)
        return customers.values.filter { it.customerType == type }
    }

    fun findTopCustomers(limit: Int): List<Customer> {
        val query = "SELECT * FROM customers ORDER BY total_spent DESC LIMIT $limit"
        DatabaseConnect.query(query)
        return customers.values.sortedByDescending { it.totalSpent }.take(limit)
    }
}