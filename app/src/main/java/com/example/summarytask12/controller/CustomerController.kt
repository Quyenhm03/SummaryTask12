package com.example.summarytask12.controller

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.extension.isValidEmail
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
                1 -> createNewCustomer()
                2 -> findCustomer()
                3 -> viewCustomerTypes()
                0 -> return
                else -> outputHandler.printError("Invalid option")
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

    private fun viewCustomerTypes() {
        outputHandler.printCustomerTypes(customerService)
    }
}
