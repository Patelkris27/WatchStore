package com.example.watchstore

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserOrdersActivity : AppCompatActivity() {

    private val list = ArrayList<Order>()
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_orders)

        rv = findViewById(R.id.rvOrders)
        rv.layoutManager = LinearLayoutManager(this)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return

        FirebaseDatabase.getInstance().reference
            .child("orders")
            .orderByChild("userId")
            .equalTo(uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    list.clear()

                    for (s in snapshot.children) {
                        list.add(
                            Order(
                                id = s.key ?: "",
                                productId = s.child("productId").value.toString(),
                                quantity = s.child("quantity").getValue(Int::class.java) ?: 0,
                                total = s.child("total").getValue(Int::class.java) ?: 0,
                                date = s.child("date").value.toString(),
                                status = s.child("status").value.toString()
                            )
                        )
                    }

                    rv.adapter = UserOrderAdapter(list)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
