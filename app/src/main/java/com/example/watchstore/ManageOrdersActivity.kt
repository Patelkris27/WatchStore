package com.example.watchstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityManageOrdersBinding
import com.google.firebase.database.*

class ManageOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageOrdersBinding
    private lateinit var db: DatabaseReference
    private val orderList = ArrayList<Order>()
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference.child("orders")

        setupRecyclerView()
        loadOrders()
    }

    private fun setupRecyclerView() {
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = OrderAdapter(orderList)
    }

    private fun loadOrders() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()

                for (s in snapshot.children) {

                    if (s.value is Map<*, *>) {

                        val order = s.getValue(Order::class.java)

                        if (order != null) {
                            orderList.add(order.copy(orderId = s.key ?: ""))
                        }
                    }
                }

                binding.rvOrders.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        db.addValueEventListener(valueEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        db.removeEventListener(valueEventListener)
    }
}
