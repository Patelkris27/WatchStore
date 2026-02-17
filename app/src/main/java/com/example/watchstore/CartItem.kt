package com.example.watchstore

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CartItem(
    val productId: String = "",
    val name: String = "",
    val quantity: Long = 0,
    val imageUrl: String = "",
    val price: Double = 0.0
) : Parcelable
