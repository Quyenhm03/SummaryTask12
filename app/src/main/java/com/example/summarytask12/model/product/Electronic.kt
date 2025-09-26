package com.example.summarytask12.model.product

class Electronic(
    id: String,
    name: String,
    price: Double,
    var warrantyMonths: Int? = null,
) : Product(id, name, price, ProductCategory.ELECTRONIC) {
    constructor(id: String, name: String, price: Double) : this(id, name, price, null)

    override fun generateDescription(): String {
        return "Electronics: $name with ${warrantyMonths?.toString() ?: "0"} warranty months"
    }

    override fun calculateDiscountProduct(amount: Double): Double {
        val months = warrantyMonths ?: 0

        return when {
            months in 12..24 -> amount * 0.01
            months in 24..36 -> amount * 0.02
            months > 36 -> amount * 0.03
            else -> 0.0
        }
    }

    override fun toString(): String {
        return super.toString() + ", Warranty: ${warrantyMonths?.toString() ?: "0"} months"
    }

    fun extendWarranty(additionalMonths: Int): Result<Unit> {
        return if (additionalMonths > 0) {
            warrantyMonths ?: 0
            warrantyMonths = warrantyMonths!! + additionalMonths
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Additional warranty months invalid"))
        }
    }
}