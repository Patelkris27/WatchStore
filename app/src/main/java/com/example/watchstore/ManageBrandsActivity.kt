package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ManageBrandsActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private val list = ArrayList<Brand>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_brands)

        val rvBrands = findViewById<RecyclerView>(R.id.rvBrands)
        val btnAddBrand = findViewById<Button>(R.id.btnAddBrand)


        db = FirebaseDatabase.getInstance().reference.child("brands")

        rvBrands.layoutManager = LinearLayoutManager(this)

        btnAddBrand.setOnClickListener {
            startActivity(Intent(this, AddBrandActivity::class.java))
        }

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (s in snapshot.children) {
                    list.add(Brand(s.key!!, s.value.toString()))
                }
                rvBrands.adapter = BrandAdapter(list)

            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
