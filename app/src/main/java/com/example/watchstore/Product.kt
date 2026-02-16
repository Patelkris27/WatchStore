package com.example.watchstore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val brandId: String = "",
    val categoryId: String = "",
    val stock: Int = 0
) : Parcelable
