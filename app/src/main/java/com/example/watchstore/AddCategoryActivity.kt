package com.example.watchstore

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import com.google.firebase.database.FirebaseDatabase

class AddCategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_category)

        val etCategory = findViewById<EditText>(R.id.etCategory)
        val btnSave = findViewById<Button>(R.id.btnSaveCategory)

        val db = FirebaseDatabase.getInstance().reference.child("categories")

        btnSave.setOnClickListener {
            val name = etCategory.text.toString().trim()
            if (name.isNotEmpty()) {
                db.push().setValue(name)
                finish()
            }
        }
    }
}
