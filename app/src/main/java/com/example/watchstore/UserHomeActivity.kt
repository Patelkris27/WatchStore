package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class UserHomeActivity : AppCompatActivity() {

    private lateinit var rvProducts: RecyclerView
    private val list = ArrayList<Product>()
    private lateinit var db: DatabaseReference
    private var selectedBrand: String? = null
    private var selectedCategory: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_user)

        rvProducts = findViewById(R.id.rvUserProducts)
        rvProducts.layoutManager = GridLayoutManager(this, 2)
        val rvBrands = findViewById<RecyclerView>(R.id.rvBrands)
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)

        rvBrands.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvCategories.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        loadFilters("brands", rvBrands) { brandId ->
            selectedBrand = brandId
            applyFilter()
        }

        loadFilters("categories", rvCategories) { categoryId ->
            selectedCategory = categoryId
            applyFilter()
        }


        db = FirebaseDatabase.getInstance().reference.child("products")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (s in snapshot.children) {
                    list.add(
                        Product(
                            id = s.key ?: "",
                            name = s.child("name").value.toString(),
                            price = s.child("price").value.toString(),
                            imageUrl = s.child("imageUrl").value.toString(),
                            brandId = s.child("brandId").value.toString(),
                            categoryId = s.child("categoryId").value.toString(),
                            stock = s.child("stock").getValue(Int::class.java) ?: 0
                        )
                    )
                }

                rvProducts.adapter = UserProductAdapter(list)
//                Toast.makeText(this@UserHomeActivity, "Loaded ${items.size} $node", Toast.LENGTH_SHORT).show()

            }

            override fun onCancelled(error: DatabaseError) {}
        })
        findViewById<ImageView>(R.id.btnCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        findViewById<Button>(R.id.btnMyOrders).setOnClickListener {
            startActivity(Intent(this, UserOrdersActivity::class.java))
        }

    }
    private fun loadFilters(
        node: String,
        rv: RecyclerView,
        onSelect: (String?) -> Unit
    ) {
        FirebaseDatabase.getInstance().reference.child(node)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val items = ArrayList<Pair<String, String>>()
                    items.add("All" to "")

                    for (s in snapshot.children) {

                        val name = when {
                            s.value is String -> s.value.toString()
                            s.child("name").exists() -> s.child("name").value.toString()
                            else -> continue
                        }

                        items.add(name to s.key!!)
                    }

                    rv.adapter = FilterAdapter(items) { id ->
                        onSelect(if (id.isNullOrEmpty()) null else id)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun applyFilter() {
        val filtered = list.filter {
            (selectedBrand == null || it.brandId == selectedBrand) &&
                    (selectedCategory == null || it.categoryId == selectedCategory)
        }
        rvProducts.adapter = UserProductAdapter(filtered)
    }

}
