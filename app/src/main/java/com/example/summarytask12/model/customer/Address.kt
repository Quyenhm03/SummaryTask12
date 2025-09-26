package com.example.summarytask12.model.customer

data class Address(
    val street: String,
    val city: String,
    val country: String = "Vietnam"
)