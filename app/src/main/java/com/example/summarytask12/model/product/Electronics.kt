package com.example.summarytask12.model.product

import com.example.summarytask12.util.DatabaseConnect

class Electronics (
    id: String,
    name: String,
    price: Double,
    private var warrantyYears: Int? = null
) : Product(id, name, price) {
    constructor(id: String, name: String, price: Double) : this(id, name, price, null)

    override fun getDescription(): String {
        return "Electronics: $name with ${warrantyYears ?: 0} year warranty costs $price VND"
    }

    override fun calculateDiscount(): Double {
        return super.calculateDiscount() * 0.5
    }

    override fun toString(): String {
        return super.toString() + ", Warranty: ${warrantyYears?.toString() ?: "0"} years"
    }

    fun getWarrantyYears(): Int? = warrantyYears
    fun updateWarranty(years: Int) {
        warrantyYears = years
    }
}