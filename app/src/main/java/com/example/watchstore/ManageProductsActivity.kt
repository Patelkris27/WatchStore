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
    private val productList = ArrayList<Product>()
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

                valueEventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        productList.clear()

                        for (s in snapshot.children) {
                            try {
                                val product = s.getValue(Product::class.java)
                                if (product != null) {
                                    productList.add(
                                        product.copy(id = s.key ?: "")
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        binding.rvProducts.adapter =
                            ProductAdapter(productList, db, brandMap, categoryMap)
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
