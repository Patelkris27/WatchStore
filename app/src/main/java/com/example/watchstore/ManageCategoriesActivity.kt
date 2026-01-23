package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ManageCategoriesActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private val list = ArrayList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_categories)

        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)
        val btnAddCategory = findViewById<Button>(R.id.btnAddCategory)

        db = FirebaseDatabase.getInstance().reference.child("categories")

        rvCategories.layoutManager = LinearLayoutManager(this)

        btnAddCategory.setOnClickListener {
            startActivity(Intent(this, AddCategoryActivity::class.java))
        }

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (s in snapshot.children) {
                    list.add(Category(s.key!!, s.value.toString()))
                }
                rvCategories.adapter = CategoryAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
