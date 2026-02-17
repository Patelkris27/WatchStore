package com.example.watchstore

data class Product(
    var id: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var stock: Long = 0,
    var imageUrl: String = "",
    var brandId: String = "",
    var categoryId: String = "",
    var quantity: Int = 0,
    var productId: String = "",
    var image: String = ""
)
