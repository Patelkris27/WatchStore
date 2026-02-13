package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityManageBrandsBinding
import com.google.firebase.database.*

class ManageBrandsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageBrandsBinding
    private lateinit var db: DatabaseReference
    private val brandList = ArrayList<Brand>()
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageBrandsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference.child("brands")

        setupRecyclerView()
        setupClickListeners()
        loadBrands()
    }

    private fun setupRecyclerView() {
        binding.rvBrands.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        binding.btnAddBrand.setOnClickListener {
            startActivity(Intent(this, AddBrandActivity::class.java))
        }
    }

    private fun loadBrands() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                brandList.clear()
                for (s in snapshot.children) {
                    brandList.add(Brand(s.key!!, s.value.toString()))
                }
                binding.rvBrands.adapter = BrandAdapter(brandList)
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
