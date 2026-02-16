package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartFragment : Fragment() {

    private val list = ArrayList<CartItem>()
    private lateinit var rv: RecyclerView
    private lateinit var tvTotal: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)

        rv = view.findViewById(R.id.rvCart)
        tvTotal = view.findViewById(R.id.tvTotal)
        val btnCheckout = view.findViewById<Button>(R.id.btnCheckout)

        rv.layoutManager = LinearLayoutManager(context)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return view
        val cartRef = FirebaseDatabase.getInstance().reference
            .child("carts").child(uid)

        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()
                var total = 0.0

                for (s in snapshot.children) {
                    val qty = s.child("quantity").getValue(Long::class.java) ?: 0L
                    val price = s.child("price").getValue(Double::class.java) ?: 0.0
                    total += qty * price

                    list.add(
                        CartItem(
                            productId = s.key!!,
                            quantity = qty,
                            price = price,
                            imageUrl = s.child("imageUrl").getValue(String::class.java) ?: ""
                        )
                    )
                }

                tvTotal.text = "Total: ₹$total"
                rv.adapter = CartAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        btnCheckout.setOnClickListener {
            startActivity(Intent(context, CheckoutActivity::class.java))
        }

        return view
    }
}