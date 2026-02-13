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

        db = FirebaseDatabase.getInstance().reference.child("products")

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
                    val product = Product(
                        id = s.key!!,
                        name = s.child("name").value.toString(),
                        price = s.child("price").value.toString(),
                        imageUrl = s.child("imageUrl").value.toString(),
                        brandId = s.child("brandId").value.toString(),
                        categoryId = s.child("categoryId").value.toString(),
                        stock = s.child("stock").getValue(Int::class.java) ?: 0
                    )
                    productList.add(product)
                }
                binding.rvProducts.adapter = ProductAdapter(productList, db)
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
