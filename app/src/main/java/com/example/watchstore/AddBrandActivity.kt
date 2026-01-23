package com.example.watchstore

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase


class AddBrandActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_brand)
        enableEdgeToEdge()

        val btnSaveBrand = findViewById<Button>(R.id.btnSaveBrand)
        val etBrandName = findViewById<EditText>(R.id.etBrandName)
        val db = FirebaseDatabase.getInstance().reference.child("brands")

        btnSaveBrand.setOnClickListener {
            val name = etBrandName.text.toString().trim()
            if (name.isNotEmpty()) {
                db.push().setValue(name)
                finish()
            }
        }
    }
}
