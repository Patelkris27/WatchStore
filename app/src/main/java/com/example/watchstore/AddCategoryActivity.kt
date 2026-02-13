package com.example.watchstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.watchstore.databinding.ActivityAddCategoryBinding
import com.google.firebase.database.FirebaseDatabase

class AddCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseDatabase.getInstance().reference.child("categories")

        binding.btnSaveCategory.setOnClickListener {
            val name = binding.etCategory.text.toString().trim()
            if (name.isNotEmpty()) {
                db.push().setValue(name)
                finish()
            }
        }
    }
}
