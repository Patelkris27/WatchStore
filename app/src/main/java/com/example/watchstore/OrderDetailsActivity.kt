package com.example.watchstore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var productAdapter: OrderProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val products = intent.getParcelableArrayListExtra<Product>("products")

        if (products != null) {
            productAdapter = OrderProductAdapter(products)
            binding.rvProducts.apply {
                layoutManager = LinearLayoutManager(this@OrderDetailsActivity)
                adapter = productAdapter
            }
        }
    }
}
