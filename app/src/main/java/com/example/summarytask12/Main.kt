package com.example.summarytask12

import com.example.summarytask12.model.product.Clothing
import com.example.summarytask12.model.product.Electronics
import com.example.summarytask12.model.product.Product
import com.example.summarytask12.repository.InventoryRepository
import com.example.summarytask12.util.DatabaseConnect

fun main() {
    testRepo()
}

fun testClass() {
    val clothing = Clothing("CLT1", "Jean", 300000.0, "M")
    println(clothing.getDescription())
    println(clothing.toString())
    println(clothing.calculateDiscount(100.0))

    val product: Product = Electronics("E1", "Fan", 30.0, 2)
    println(product.getDescription())
}

// pass
fun testRepo() {
    DatabaseConnect.connect()

    val clothing = Clothing("CLT1", "Jean", 300000.0, "M")
    val repo = InventoryRepository()
    repo.addProduct(clothing)

    val products = repo.getProducts()
    println(products)
}