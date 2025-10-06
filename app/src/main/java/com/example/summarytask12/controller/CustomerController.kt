package com.example.summarytask12.controller

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.extension.isValidEmail
import com.example.summarytask12.model.customer.Address
import com.example.summarytask12.service.CustomerService
import com.example.summarytask12.util.InputHandler
import com.example.summarytask12.util.OutputHandler

class CustomerController(
    private val customerService: CustomerService,
    private val inputHandler: InputHandler,
    private val outputHandler: OutputHandler
) {

    @RequiresApi(Build.VERSION_CODES.O)
    fun customerManagement() {
        while (true) {
            outputHandler.printCustomerMenu()
            when (inputHandler.readInt("\nOption: ") ?: -1) {
                1 -> {
                    createNewCustomer()
                }
                2 -> {
                    findCustomer()
                }
                3 -> {
                    updateCustomer()
                }
                4 -> {
                    deleteCustomer()
                }
                5 -> {
                    viewAllCustomers()
                }
                6 -> {
                    viewTopCustomers()
                }
                7 -> {
                    viewCustomerTypes()
                }
                8 -> {
                    viewCustomerReport()
                }
                0 -> {
                    return
                }
                else -> {
                    outputHandler.printError("Invalid option")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNewCustomer() {
        outputHandler.printSuccess("\nCreate New Customer")
        val name = inputHandler.readLine("Customer Name: ") ?: return
        var email: String
        do {
            email = inputHandler.readLine("Email: ") ?: return
            if (!email.isValidEmail()) {
                outputHandler.printError("Invalid email format")
            }
        } while (!email.isValidEmail())
        val phone = inputHandler.readLine("Phone (optional): ")

        customerService.createCustomer(name, email, phone).onSuccess { customer ->
            outputHandler.printSuccess("Customer created successfully!")
            outputHandler.printSuccess("Customer ID: ${customer.id}")
        }.onFailure {
            outputHandler.printError("Error: ${it.message}")
        }
    }

    private fun findCustomer() {
        outputHandler.printSuccess("\nFind Customer")
        val id = inputHandler.readLine("Customer ID: ") ?: return
        val customer = customerService.findById(id)
        if (customer != null) {
            outputHandler.printCustomerDetails(customer)
        } else {
            outputHandler.printError("Customer not found")
        }
    }

    private fun updateCustomer() {
        outputHandler.printSuccess("\nUpdate Customer Info")
        val id = inputHandler.readLine("Customer ID: ") ?: return
        val existing = customerService.findById(id)

        if (existing == null) {
            outputHandler.printError("Customer not found")
            return
        }

        val newEmail = inputHandler.readLine("New Email (${existing.email}): ") ?: existing.email
        val newPhone = inputHandler.readLine("New Phone (${existing.phone ?: "N/A"}): ") ?: existing.phone
        val street = inputHandler.readLine("Street: ") ?: return
        val city = inputHandler.readLine("City: ") ?: return
        val country = inputHandler.readLine("Country (default: Vietnam): ") ?: "Vietnam"

        val newAddress = Address(street, city, country)

        existing.updateContactInfo(newEmail, newPhone ?: "", newAddress).onSuccess {
            customerService.update(existing)
            outputHandler.printSuccess("Customer updated successfully!")
        }.onFailure {
            outputHandler.printError("Error: ${it.message}")
        }
    }

    private fun deleteCustomer() {
        outputHandler.printSuccess("\nDelete Customer")
        val id = inputHandler.readLine("Customer ID: ") ?: return
        customerService.findById(id)?.let {
            println("Are you sure you want to delete ${it.name}? (y/n): ")
            val confirm = readlnOrNull()
            if (confirm.equals("y", true)) {
                customerService.deleteCustomerById(id).onSuccess {
                    outputHandler.printSuccess("Customer deleted successfully!")
                }.onFailure {
                    outputHandler.printError("Error: ${it.message}")
                }
            }
        } ?: outputHandler.printError("Customer not found")
    }

    private fun viewAllCustomers() {
        outputHandler.printSuccess("\nAll Customers")
        val allCustomers = customerService.findAll()
        if (allCustomers.isEmpty()) {
            outputHandler.printError("No customers found")
        } else {
            allCustomers.forEachIndexed { index, c ->
                println("${index + 1}. ${c.name} | ${c.email} | ${c.customerType.displayName} | ${c.totalSpent.toInt()} VND")
            }
        }
    }

    private fun viewTopCustomers() {
        val limit = inputHandler.readInt("Enter number of top customers: ") ?: 5
        val topCustomers = customerService.findTopCustomers(limit)
        if (topCustomers.isEmpty()) {
            outputHandler.printError("No customers found")
        } else {
            outputHandler.printSuccess("\nTop $limit Customers by Spending")
            topCustomers.forEachIndexed { index, c ->
                println("${index + 1}. ${c.name} - ${c.totalSpent.toInt()} VND (${c.customerType.displayName})")
            }
        }
    }

    private fun viewCustomerTypes() {
        outputHandler.printCustomerTypes(customerService)
    }

    private fun viewCustomerReport() {
        outputHandler.printSuccess("\nView Customer Report")
        val id = inputHandler.readLine("Enter Customer ID: ") ?: return

        val customer = customerService.findById(id)
        if (customer == null) {
            outputHandler.printError("Customer not found")
            return
        }

        val report = customer.generateReport()

        println("\n" + "=".repeat(60))
        println("CUSTOMER REPORT")
        println("=".repeat(60))

        report.forEach { (key, value) ->
            println("${key.replaceFirstChar { it.uppercase() }}: $value")
        }

        println("=".repeat(60))
    }
}
