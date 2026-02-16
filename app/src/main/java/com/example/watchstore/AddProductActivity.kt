package com.example.watchstore

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.watchstore.databinding.ActivityAddProductBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private val brandList = ArrayList<String>()
    private val categoryList = ArrayList<String>()
    private val brandMap = HashMap<String, String>()
    private val categoryMap = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadCategories()
        loadBrands()

        binding.btnSaveProduct.setOnClickListener {
            addProduct()
        }
    }

    private fun addProduct() {
        val name = binding.etProductName.text.toString().trim()
        val price = binding.etPrice.text.toString().trim()
        val stock = binding.etStock.text.toString().trim()
        val imageUrl = binding.etImageUrl.text.toString().trim()
        val brandName = binding.spBrand.selectedItem.toString()
        val categoryName = binding.spCategory.selectedItem.toString()

        if (name.isEmpty() || price.isEmpty() || stock.isEmpty() || imageUrl.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
            return
        }

        val brandId = brandMap.entries.find { it.value == brandName }?.key
        val categoryId = categoryMap.entries.find { it.value == categoryName }?.key

        if (brandId == null || categoryId == null) {
            Toast.makeText(this, "Invalid brand or category", Toast.LENGTH_SHORT).show()
            return
        }

        val db = FirebaseDatabase.getInstance().reference
        val id = db.child("products").push().key!!

        val product = Product(
            id = id,
            name = name,
            price = price.toDouble(),
            stock = stock.toLong(),
            imageUrl = imageUrl,
            brandId = brandId,
            categoryId = categoryId
        )

        db.child("products").child(id).setValue(product).addOnCompleteListener {
            if (it.isSuccessful) {
                Toast.makeText(this, "Product added successfully", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Failed to add product", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadBrands() {
        FirebaseDatabase.getInstance().reference.child("brands")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    brandList.clear()
                    brandMap.clear()
                    for (item in snapshot.children) {
                        brandMap[item.key!!] = item.value.toString()
                        brandList.add(item.value.toString())
                    }
                    val adapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_spinner_item, brandList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spBrand.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadCategories() {
        FirebaseDatabase.getInstance().reference.child("categories")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    categoryList.clear()
                    categoryMap.clear()
                    for (item in snapshot.children) {
                        categoryMap[item.key!!] = item.value.toString()
                        categoryList.add(item.value.toString())
                    }
                    val adapter = ArrayAdapter(this@AddProductActivity, android.R.layout.simple_spinner_item, categoryList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.spCategory.adapter = adapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
