package com.example.watchstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var orderDetailsAdapter: OrderDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getStringExtra("orderId")
        val productList = intent.getParcelableArrayListExtra<CartItem>("products")

        if (orderId != null && productList != null) {
            val products = productList.map {
                Product(
                    productId = it.productId,
                    name = it.name,
                    price = it.price,
                    imageUrl = it.imageUrl,
                    quantity = it.quantity.toInt(),
                    image = it.imageUrl
                )
            }
            orderDetailsAdapter = OrderDetailsAdapter(this, products.toMutableList(), orderId)
            binding.rvOrderDetails.layoutManager = LinearLayoutManager(this)
            binding.rvOrderDetails.adapter = orderDetailsAdapter
        }
    }
}
