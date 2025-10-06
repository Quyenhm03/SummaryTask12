package com.example.summarytask12.service

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.model.customer.Customer
import com.example.summarytask12.model.customer.CustomerType
import com.example.summarytask12.repository.CustomerRepository

class CustomerService(val customerRepository: CustomerRepository) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun create(customer: Customer): Result<Customer> {
        return customerRepository.save(customer).onSuccess { savedCustomer ->
            savedCustomer.sendNotification(
                "Welcome to our store!",
                format = { id, name, msg -> "[$id] Hello $name: $msg" }
            )
        }
    }

    fun findById(id: String): Customer? = customerRepository.findById(id)

    fun findAll(): List<Customer> = customerRepository.findAll()

    fun update(customer: Customer): Result<Customer> = customerRepository.update(customer)

    @RequiresApi(Build.VERSION_CODES.O)
    fun createCustomer(name: String, email: String, phone: String? = null): Result<Customer> {
        return Customer.create(name, email, phone).onSuccess { customer ->
            create(customer)
        }
    }

    fun findByEmail(email: String): Customer? = customerRepository.findByEmail(email)

    fun getCustomersByType(type: CustomerType): List<Customer> {
        return customerRepository.findByType(type)
    }

    fun deleteCustomerById(id: String) = customerRepository.deleteById(id)

    fun findTopCustomers(limit: Int) = customerRepository.findTopCustomers(limit)
}