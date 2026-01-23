package com.example.watchstore

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ManageOrdersActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var rvOrders: RecyclerView
    private val list = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_orders)

        rvOrders = findViewById(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)

        db = FirebaseDatabase.getInstance().reference.child("orders")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (s in snapshot.children) {
                    val userId = s.child("userId").value.toString()
                    val total = s.child("total").value.toString()
                    val status = s.child("status").value.toString()
                    list.add(Order(s.key!!, userId, total, status))
                }
                rvOrders.adapter = OrderAdapter(list, db)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
