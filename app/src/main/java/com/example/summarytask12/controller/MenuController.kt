package com.example.summarytask12.controller

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.summarytask12.StoreApplication
import com.example.summarytask12.extension.isValidEmail
import com.example.summarytask12.model.customer.Customer
import com.example.summarytask12.model.customer.CustomerType
import com.example.summarytask12.model.order.Order
import com.example.summarytask12.model.product.Clothing
import com.example.summarytask12.model.product.ClothingSize
import com.example.summarytask12.model.product.Electronic
import com.example.summarytask12.model.product.ProductCategory
import com.example.summarytask12.service.CustomerService
import com.example.summarytask12.service.OrderService
import com.example.summarytask12.service.ProductService
import com.example.summarytask12.util.DatabaseConnect
import com.example.summarytask12.util.InputHandler
import com.example.summarytask12.util.OutputHandler
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

class MenuController(
    private val productController: ProductController,
    private val customerController: CustomerController,
    private val orderController: OrderController,
    private val reportController: ReportController,
    private val inputHandler: InputHandler,
    private val outputHandler: OutputHandler
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun showMainMenu() {
        while (true) {
            outputHandler.printMainMenu(StoreApplication.currentRevenue)
            val choice = inputHandler.readInt("Choose an option (1-5): ") ?: 0

            when (choice) {
                1 -> productController.productManagement()
                2 -> customerController.customerManagement()
                3 -> orderController.orderManagement()
                4 -> reportController.reportsAndAnalytics()
                5 -> {
                    outputHandler.printExitMessage(StoreApplication.currentRevenue)
                    DatabaseConnect.disconnect()
                    exitProcess(0)
                }

                else -> outputHandler.printError("Invalid option, please try again.")
            }

            outputHandler.printContinuePrompt()
            inputHandler.readLine()
        }
    }

}