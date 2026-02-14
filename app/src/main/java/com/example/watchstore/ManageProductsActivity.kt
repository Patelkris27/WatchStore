package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityManageProductsBinding
import com.google.firebase.database.*

class ManageProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageProductsBinding
    private lateinit var db: DatabaseReference
    private val productList = ArrayList<Product>()
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference

        setupRecyclerView()
        setupClickListeners()
        loadProducts()
    }

    private fun setupRecyclerView() {
        binding.rvProducts.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        binding.btnAddProducts.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }
    }

    private fun loadProducts() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (s in snapshot.children) {
                    val product = s.getValue(Product::class.java)
                    if (product != null) {
                        productList.add(product)
                    }
                }
                binding.rvProducts.adapter = ProductAdapter(productList, db, emptyMap(), emptyMap())
                binding.rvProducts.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        db.child("products").addValueEventListener(valueEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        db.child("products").removeEventListener(valueEventListener)
    }
}
