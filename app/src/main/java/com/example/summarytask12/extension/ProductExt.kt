package com.example.summarytask12.extension

import com.example.summarytask12.model.product.Product
import com.example.summarytask12.model.product.ProductCategory

fun Product.matches(
    query: String,
    minPrice: Double?,
    maxPrice: Double?,
    category: ProductCategory?
): Boolean {
    val nameMatches = name.contains(query, ignoreCase = true)
    val descriptionMatches = generateDescription().contains(query, ignoreCase = true) ?: false
    if (!nameMatches && !descriptionMatches) {
        return false
    }

    if (minPrice != null && price < minPrice) {
        return false
    }
    if (maxPrice != null && price > maxPrice) {
        return false
    }

    if (category != null && this.category != category) {
        return false
    }

    return true
}
