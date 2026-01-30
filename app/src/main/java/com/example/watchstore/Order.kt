package com.example.watchstore

data class Order(
    val id: String = "",
    val user: String = "",
    val productId: String = "",
    val quantity: Int = 0,
    val total: Int = 0,
    val date: String = "",
    val status: String = "Pending"
)
