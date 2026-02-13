package com.example.watchstore

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.watchstore.databinding.ActivityAddProductBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddProductBinding
    private lateinit var db: DatabaseReference
    private val brandMap = HashMap<String, String>()
    private val categoryMap = HashMap<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference

        loadSpinners()
        setupClickListeners()
    }

    private fun loadSpinners() {
        loadSpinnerData(db.child("brands"), binding.spBrand, brandMap)
        loadSpinnerData(db.child("categories"), binding.spCategory, categoryMap)
    }

    private fun loadSpinnerData(
        ref: DatabaseReference,
        spinner: Spinner,
        map: HashMap<String, String>
    ) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = ArrayList<String>()
                for (s in snapshot.children) {
                    val name = s.value.toString()
                    s.key?.let {
                        map[name] = it
                        list.add(name)
                    }
                }
                spinner.adapter =
                    ArrayAdapter(this@AddProductActivity, android.R.layout.simple_spinner_dropdown_item, list)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddProductActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnSaveProduct.setOnClickListener {
            if (validateInput()) {
                saveProduct()
            }
        }
    }

    private fun validateInput(): Boolean {
        if (binding.etProductName.text.isEmpty() || binding.etPrice.text.isEmpty()
            || binding.etImageUrl.text.isEmpty() || binding.etStock.text.isEmpty()
        ) {
            Toast.makeText(this, getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun saveProduct() {
        val name = binding.etProductName.text.toString()
        val price = binding.etPrice.text.toString()
        val imageUrl = binding.etImageUrl.text.toString()
        val stock = binding.etStock.text.toString().toIntOrNull() ?: 0
        val selectedBrandName = binding.spBrand.selectedItem.toString()
        val selectedCategoryName = binding.spCategory.selectedItem.toString()
        val brandId = brandMap[selectedBrandName]
        val categoryId = categoryMap[selectedCategoryName]

        if (brandId == null || categoryId == null) {
            Toast.makeText(this, "Please select a brand and category", Toast.LENGTH_SHORT).show()
            return
        }

        val productId = db.child("products").push().key ?: return

        val product = Product(productId, name, price, imageUrl, brandId, categoryId, stock)

        db.child("products").child(productId).setValue(product).addOnCompleteListener {
            if (it.isSuccessful) {
                finish()
            } else {
                Toast.makeText(this, "Failed to save product", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
