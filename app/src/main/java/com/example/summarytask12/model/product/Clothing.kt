package com.example.summarytask12.model.product

class Clothing(
    id: String,
    name: String,
    price: Double,
    val size: ClothingSize,
    val brand: String = "Generic"
) : Product(id, name, price, ProductCategory.CLOTHING) {

    var season: String? = null
    var gender: String = "Unisex"

    constructor(id: String, name: String, price: Double, size: String) : this(
        id, name, price, when (size.uppercase()) {
            "S" -> ClothingSize.S
            "M" -> ClothingSize.M
            "L" -> ClothingSize.L
            "XL" -> ClothingSize.XL
            else -> throw IllegalArgumentException("Invalid size: $size")
        }
    )

    override fun generateDescription(): String {
        val seasonInfo = season?.let { " for $it" } ?: ""
        val genderInfo = if (gender != "Unisex") " - $gender" else ""

        return "Clothing: $brand $name, size ${size.displayName} $seasonInfo$genderInfo"
    }

    override fun calculateDiscountProduct(amount: Double): Double =
        if (size == ClothingSize.XL) amount * 0.01 else 0.0

}
