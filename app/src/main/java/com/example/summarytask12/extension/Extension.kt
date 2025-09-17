package com.example.summarytask12.extension

fun String?.isValidEmail() : Boolean {
    if (this == null) {
        return false
    }

    if (this.contains("@") && this.contains(".")) {
        return true
    }

    return false
}
