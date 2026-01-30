package com.example.watchstore

data class CartItem(
    val productId: String = "",
    val quantity: Int = 0,
    val imageUrl: String = "",
    val price: Int = 0
)
