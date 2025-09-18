package com.example.summarytask12.extension

fun String?.isValidEmail() : Boolean {
    if (this == null) {
        return false
    }

    if (this.matches(Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))) {
        return true
    }

    return false
}

fun String?.capitalizeFirst() {
    this?.replaceFirstChar {
        it.uppercaseChar()
    }
}
