package com.example.summarytask12.extension

import com.example.summarytask12.model.Customer

fun Customer.getCustomerSegment(age: Int): String = when {
    age < 18 -> "Youth"
    age < 65 -> "Adult"
    else -> "Senior"
}