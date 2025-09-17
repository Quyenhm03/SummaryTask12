package com.example.summarytask12.util

object DatabaseConnect {
    private var isConnected: Boolean = false

    fun connect() {
        if (!isConnected) {
            isConnected = true
            println("Database connected")
        }
    }

    fun disconnect() {
        if (isConnected) {
            isConnected = false
            println("Database disconnected")
        }
    }

    fun isConnected() = isConnected

    fun query(statement: String): Any? {
        return if (isConnected) {
            println(" Executing query: $statement")
            when {
                statement.contains("SELECT product") -> "Product data"
                statement.contains("SELECT electronics") -> "Electronics data"
                statement.contains("SELECT clothing") -> "Clothing data"
                statement.contains("SELECT customer") -> "Customer data"
                statement.contains("INSERT") -> "Data inserted"
                statement.contains("UPDATE") -> "Data updated"
                else -> null
            }
        } else {
            println("Error: No database connected")
            null
        }
    }
}