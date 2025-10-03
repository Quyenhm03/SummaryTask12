package com.example.summarytask12.controller

import com.example.summarytask12.service.OrderService
import com.example.summarytask12.service.ProductService
import com.example.summarytask12.util.InputHandler
import com.example.summarytask12.util.OutputHandler

class ReportController(
    private val productService: ProductService,
    private val orderService: OrderService,
    private val inputHandler: InputHandler,
    private val outputHandler: OutputHandler
) {

    fun reportsAndAnalytics() {
        while (true) {
            outputHandler.printReportsMenu()
            when (inputHandler.readInt("\nOption: ") ?: -1) {
                1 -> showInventoryReport()
                2 -> showSalesReport()
                3 -> showRevenueSummary()
                0 -> return
                else -> outputHandler.printError("Invalid option")
            }
        }
    }

    private fun showInventoryReport() {
        outputHandler.printSuccess("\nInventory Report")
        val report = productService.generateInventoryReport()
        outputHandler.printInventoryReport(report)
    }

    private fun showSalesReport() {
        outputHandler.printSuccess("\nSales Report")
        val report = orderService.generateSalesReport()
        outputHandler.printSalesReport(report)
    }

    private fun showRevenueSummary() {
        outputHandler.printSuccess("\nRevenue Summary")
        val totalRevenue = orderService.getTotalRevenue()
        val orders = orderService.findAll()
        outputHandler.printRevenueSummary(totalRevenue, orders)
    }
}