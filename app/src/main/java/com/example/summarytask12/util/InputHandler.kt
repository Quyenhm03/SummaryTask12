package com.example.summarytask12.util

class InputHandler {
    fun readLine(prompt: String? = null): String? {
        if (prompt != null) print(prompt)
        return readlnOrNull()?.takeIf { it.isNotBlank() }
    }

    fun readInt(prompt: String): Int? {
        val input = readLine(prompt)
        return input?.toIntOrNull()
    }

    fun readDouble(prompt: String): Double? {
        val input = readLine(prompt)
        return input?.toDoubleOrNull()
    }
}