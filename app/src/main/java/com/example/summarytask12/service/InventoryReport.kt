package com.example.summarytask12.service

import com.example.summarytask12.model.product.ProductCategory

data class InventoryReport(
    val totalProducts: Int,
    val totalValue: Double,
    val categoryBreakdown: Map<ProductCategory, Int>
)