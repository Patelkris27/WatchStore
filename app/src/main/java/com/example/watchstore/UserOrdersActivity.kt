package com.example.watchstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityUserOrdersBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserOrdersBinding
    private lateinit var db: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val orderList = ArrayList<Order>()
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference.child("orders")

        setupRecyclerView()
        loadUserOrders()
    }

    private fun setupRecyclerView() {
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
        binding.rvOrders.adapter = UserOrderAdapter(orderList)
    }

    private fun loadUserOrders() {
        val uid = auth.currentUser?.uid ?: return

        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()

                for (s in snapshot.children) {
                    val order = s.getValue(Order::class.java)
                    if (order != null && order.userId == uid) {
                        orderList.add(order)
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
