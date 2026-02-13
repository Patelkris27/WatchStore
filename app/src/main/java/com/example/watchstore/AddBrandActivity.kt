package com.example.watchstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.watchstore.databinding.ActivityAddBrandBinding
import com.google.firebase.database.FirebaseDatabase

class AddBrandActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBrandBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBrandBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseDatabase.getInstance().reference.child("brands")

        binding.btnSaveBrand.setOnClickListener {
            val name = binding.etBrandName.text.toString().trim()
            if (name.isNotEmpty()) {
                db.push().setValue(name)
                finish()
            }
        }
    }
}
