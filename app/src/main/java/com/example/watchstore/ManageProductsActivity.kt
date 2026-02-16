package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityManageProductsBinding
import com.google.firebase.database.*

class ManageProductsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageProductsBinding
    private lateinit var db: DatabaseReference
    private lateinit var adapter: ProductAdapter
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageProductsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference

        binding.rvProducts.layoutManager = LinearLayoutManager(this)

        binding.btnAddProducts.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        loadProducts()
    }

    private fun loadProducts() {

        val categoryMap = mutableMapOf<String, String>()
        val brandMap = mutableMapOf<String, String>()

        db.child("categories").get().addOnSuccessListener { categorySnapshot ->

            for (s in categorySnapshot.children) {
                categoryMap[s.key!!] = s.value.toString()
            }

            db.child("brands").get().addOnSuccessListener { brandSnapshot ->

                for (s in brandSnapshot.children) {
                    brandMap[s.key!!] = s.value.toString()
                }

                adapter = ProductAdapter(db, brandMap, categoryMap)
                binding.rvProducts.adapter = adapter

                valueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val newProductList = ArrayList<Product>()

                        for (s in snapshot.children) {
                            try {
                                val product = s.getValue(Product::class.java)
                                if (product != null) {
                                    newProductList.add(
                                        product.copy(id = s.key ?: "")
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        adapter.setData(newProductList)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                }

                db.child("products").addValueEventListener(valueEventListener)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::valueEventListener.isInitialized) {
            db.child("products").removeEventListener(valueEventListener)
        }
    }
}
