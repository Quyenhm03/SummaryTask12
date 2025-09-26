package com.example.summarytask12.model.discount

import com.example.summarytask12.model.customer.CustomerType

data class DiscountContext(
    val customerType: CustomerType = CustomerType.BRONZE,
    val isFirstPurchase: Boolean = false,
    val orderTotal: Double = 0.0
)