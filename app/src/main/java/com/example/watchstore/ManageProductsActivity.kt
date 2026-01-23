package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import com.google.firebase.database.*

class ManageProductsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private lateinit var rvProducts: RecyclerView
    private lateinit var btnAddProduct: Button
    private val list = ArrayList<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_products)

        rvProducts = findViewById(R.id.rvProducts)
        btnAddProduct = findViewById(R.id.btnAddProducts)

        db = FirebaseDatabase.getInstance().reference.child("products")

        rvProducts.layoutManager = LinearLayoutManager(this)

        btnAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()

                for (s in snapshot.children) {
                    list.add(
                        Product(
                            id = s.key!!,
                            name = s.child("name").value.toString(),
                            price = s.child("price").value.toString(),
                            imageUrl = s.child("imageUrl").value.toString(),
                            brandId = s.child("brandId").value.toString(),
                            categoryId = s.child("categoryId").value.toString(),
                            stock = s.child("stock").getValue(Int::class.java) ?: 0
                        )
                    )
                }

                rvProducts.adapter =
                    ProductAdapter(list, FirebaseDatabase.getInstance().reference)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
