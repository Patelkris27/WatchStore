package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {

    private val list = ArrayList<CartItem>()
    private lateinit var rv: RecyclerView
    private lateinit var tvTotal: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)

        rv = findViewById(R.id.rvCart)
        tvTotal = findViewById(R.id.tvTotal)
        val btnCheckout = findViewById<Button>(R.id.btnCheckout)

        rv.layoutManager = LinearLayoutManager(this)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = FirebaseDatabase.getInstance().reference
            .child("carts").child(uid)

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()
                var total = 0

                for (s in snapshot.children) {
                    val qty = s.child("quantity").getValue(Int::class.java) ?: 0
                    val price = s.child("price").getValue(Int::class.java) ?: 0
                    total += qty * price

                    list.add(
                        CartItem(
                            productId = s.key!!,
                            quantity = qty,
                            price = price
                        )
                    )
                }

                tvTotal.text = "Total: ₹$total"
                rv.adapter = CartAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        btnCheckout.setOnClickListener {
            startActivity(Intent(this, CheckoutActivity::class.java))
        }
    }
}
