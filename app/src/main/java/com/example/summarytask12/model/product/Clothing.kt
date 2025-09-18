package com.example.summarytask12.model.product

import com.example.summarytask12.util.ClothingSize
import com.example.summarytask12.util.DatabaseConnect

class Clothing(
    id: String,
    name: String,
    price: Double,
    private val size: ClothingSize
) : Product(id, name, price) {
    constructor(id: String, name: String, price: Double, size: String) : this(id, name, price, when (size.uppercase()) {
        "S" -> ClothingSize.S
        "M" -> ClothingSize.M
        "L" -> ClothingSize.L
        "XL" -> {
            ClothingSize.XL
        }
        else -> throw IllegalArgumentException("Invalid size: $size")
    })

    override fun getDescription(): String = "Clothing: $name (Size: $size)"
    override fun calculateDiscount(amount: Double): Double {
        return if (size == ClothingSize.XL) {
            amount * 0.05
        } else {
            super.calculateDiscount(amount)
        }
    }

    fun getSize(): ClothingSize = size
}
