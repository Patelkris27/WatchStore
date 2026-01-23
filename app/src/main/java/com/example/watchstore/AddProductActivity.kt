package com.example.watchstore

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class AddProductActivity : AppCompatActivity() {

    private lateinit var brandMap: HashMap<String, String>
    private lateinit var categoryMap: HashMap<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_product)

        val etName = findViewById<EditText>(R.id.etProductName)
        val etPrice = findViewById<EditText>(R.id.etPrice)
        val etImage = findViewById<EditText>(R.id.etImageUrl)
        val etStock = findViewById<EditText>(R.id.etStock)
        val spBrand = findViewById<Spinner>(R.id.spBrand)
        val spCategory = findViewById<Spinner>(R.id.spCategory)
        val btnSave = findViewById<Button>(R.id.btnSaveProduct)

        val db = FirebaseDatabase.getInstance().reference
        brandMap = HashMap()
        categoryMap = HashMap()

        loadSpinner(db.child("brands"), spBrand, brandMap)
        loadSpinner(db.child("categories"), spCategory, categoryMap)

        btnSave.setOnClickListener {

            if (etName.text.isEmpty() || etPrice.text.isEmpty()
                || etImage.text.isEmpty() || etStock.text.isEmpty()
            ) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val id = db.child("products").push().key!!

            db.child("products").child(id).setValue(
                mapOf(
                    "name" to etName.text.toString(),
                    "price" to etPrice.text.toString(),
                    "imageUrl" to etImage.text.toString(),
                    "brandId" to brandMap[spBrand.selectedItem.toString()]!!,
                    "categoryId" to categoryMap[spCategory.selectedItem.toString()]!!,
                    "stock" to etStock.text.toString().toInt()
                )
            )
            finish()
        }
    }

    private fun loadSpinner(ref: DatabaseReference, spinner: Spinner, map: HashMap<String, String>) {
        ref.get().addOnSuccessListener {
            val list = ArrayList<String>()
            for (s in it.children) {
                val name = s.value.toString()
                map[name] = s.key!!
                list.add(name)
            }
            spinner.adapter =
                ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list)
        }
    }
}
