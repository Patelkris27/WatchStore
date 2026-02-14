package com.example.watchstore

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val products: List<Product> = emptyList(),
    val totalPrice: Double = 0.0,
    val status: String = "Pending"
)
