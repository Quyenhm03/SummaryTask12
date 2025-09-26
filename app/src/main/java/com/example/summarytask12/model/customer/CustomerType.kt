package com.example.summarytask12.model.customer

enum class CustomerType(
    val displayName: String,
    val discountRate: Double,
    val minSpending: Double
) {
    BRONZE("Bronze", 0.0, 0.0),
    SILVER("Silver", 0.05, 5_000_000.0),
    GOLD("Gold", 0.10, 20_000_000.0),
    PLATINUM("Platinum", 0.15, 50_000_000.0);

    fun getNextType(): CustomerType? = when (this) {
        BRONZE -> SILVER
        SILVER -> GOLD
        GOLD -> PLATINUM
        PLATINUM -> null
    }
}