package com.example.watchstore

data class CartItem(
    val productId: String = "",
    val quantity: Long = 0,
    val imageUrl: String = "",
    val price: Double = 0.0
)
