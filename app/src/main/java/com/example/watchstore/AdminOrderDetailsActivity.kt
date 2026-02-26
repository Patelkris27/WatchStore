package com.example.watchstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityAdminOrderDetailsBinding
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AdminOrderDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminOrderDetailsBinding
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminOrderDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val orderId = intent.getStringExtra("orderId") ?: return
        db = FirebaseDatabase.getInstance().reference.child("orders").child(orderId)

        setupRecyclerView()
        loadOrderDetails()
    }

    private fun setupRecyclerView() {
        binding.rvOrderProducts.layoutManager = LinearLayoutManager(this)
    }

    private fun loadOrderDetails() {
        db.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val order = snapshot.getValue(Order::class.java)
                if (order != null) {
                    displayOrderDetails(order)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun displayOrderDetails(order: Order) {
        binding.tvOrderId.text = "Order ID: #${order.orderId}"
        binding.tvOrderDate.text = "Date: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(order.orderDate))}"
        binding.tvOrderStatus.text = "Status: ${order.status}"
        
        binding.tvCustomerName.text = "Name: ${order.name}"
        binding.tvCustomerPhone.text = "Phone: ${order.phone}"
        binding.tvCustomerAddress.text = "Address: ${order.address}"

        val products = order.products.map {
            Product(
                productId = it.productId,
                name = it.name,
                price = it.price,
                imageUrl = it.imageUrl,
                quantity = it.quantity.toInt(),
                image = it.imageUrl
            )
        }

        binding.rvOrderProducts.adapter = OrderProductAdapter(products)
        binding.tvTotalAmount.text = "Total Amount: ₹${order.totalPrice}"
    }
}
