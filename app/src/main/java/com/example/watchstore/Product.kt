package com.example.watchstore

data class Product(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val imageUrl: String = "",
    val brandId: String = "",
    val categoryId: String = "",
    val stock: Int = 0
)
