package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityManageCategoriesBinding
import com.google.firebase.database.*

class ManageCategoriesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageCategoriesBinding
    private lateinit var db: DatabaseReference
    private val categoryList = ArrayList<Category>()
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference.child("categories")

        setupRecyclerView()
        setupClickListeners()
        loadCategories()
    }

    private fun setupRecyclerView() {
        binding.rvCategories.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        binding.btnAddCategory.setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }
    }

    private fun loadCategories() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()
                for (s in snapshot.children) {
                    categoryList.add(Category(s.key!!, s.value.toString()))
                }
                binding.rvCategories.adapter = CategoryAdapter(categoryList)
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
