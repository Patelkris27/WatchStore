package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class ProductDetailsActivity : AppCompatActivity() {

    private var stock = 0
    private var price = 0.0
    private var selectedQty = 1
    private var productName = ""
    private var productImageUrl = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_details)

        val img = findViewById<ImageView>(R.id.imgProduct)
        val tvName = findViewById<TextView>(R.id.tvName)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)
        val tvBrand = findViewById<TextView>(R.id.tvBrand)
        val tvCategory = findViewById<TextView>(R.id.tvCategory)
        val tvStock = findViewById<TextView>(R.id.tvStock)
        val btnBuy = findViewById<Button>(R.id.btnBuyNow)

        val productId = intent.getStringExtra("id") ?: return
        val db = FirebaseDatabase.getInstance().reference
        val btnPlus = findViewById<Button>(R.id.btnPlus)
        val btnMinus = findViewById<Button>(R.id.btnMinus)
        val tvQty = findViewById<TextView>(R.id.tvQty)

        tvQty.text = selectedQty.toString()

        btnPlus.setOnClickListener {
            if (selectedQty < stock) {
                selectedQty++
                tvQty.text = selectedQty.toString()
            }
        }

        btnMinus.setOnClickListener {
            if (selectedQty > 1) {
                selectedQty--
                tvQty.text = selectedQty.toString()
            }
        }


        db.child("products").child(productId).get().addOnSuccessListener { s ->

            productName = s.child("name").value.toString()
            tvName.text = productName
            price = s.child("price").getValue(Double::class.java) ?: 0.0
            stock = s.child("stock").getValue(Int::class.java) ?: 0
            productImageUrl = s.child("imageUrl").value.toString()

            tvPrice.text = "₹$price"

            tvStock.text = when {
                stock == 0 -> "Out of Stock"
                stock <= 5 -> "Low Stock"
                else -> "In Stock"
            }

            Glide.with(this)
                .load(productImageUrl)
                .into(img)

            loadName("brands", s.child("brandId").value.toString(), tvBrand)
            loadName("categories", s.child("categoryId").value.toString(), tvCategory)

            btnBuy.isEnabled = stock > 0
        }
        val btnCart = findViewById<Button>(R.id.btnAddToCart)

        btnCart.setOnClickListener {
            addToCart(productId)
        }

        btnBuy.setOnClickListener {
            confirmOrder(productId)
        }
    }
    private fun addToCart(productId: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseDatabase.getInstance().reference

        if (stock <= 0) {
            Toast.makeText(this, "Out of stock", Toast.LENGTH_SHORT).show()
            return
        }

        val cartRef = db.child("carts").child(uid).child(productId)

        cartRef.get().addOnSuccessListener { s ->
            val currentQty = s.child("quantity").getValue(Int::class.java) ?: 0

            cartRef.setValue(
                mapOf(
                    "quantity" to currentQty + selectedQty,
                    "price" to price
                )
            )

            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmOrder(productId: String) {
        AlertDialog.Builder(this)
            .setTitle("Confirm Order")
            .setMessage("Place order for this watch?")
            .setPositiveButton("Yes") { _, _ ->
                placeOrder(productId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun placeOrder(productId: String) {

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseDatabase.getInstance().reference

        if (stock < selectedQty) {
            Toast.makeText(this, "Out of stock", Toast.LENGTH_SHORT).show()
            return
        }

        val orderId = db.child("orders").push().key!!

        val product = Product(
            id = productId,
            name = productName,
            price = price,
            imageUrl = productImageUrl,
            stock = selectedQty
        )
        
        val cartList = ArrayList<Product>()
        cartList.add(product)

        val totalPrice = price * selectedQty

        val order = Order(
            orderId = orderId,
            userId = uid,
            products = cartList,
            totalPrice = totalPrice,
            status = "Pending"
        )

        db.child("orders").child(orderId).setValue(order)
            .addOnSuccessListener {

                db.child("products").child(productId)
                    .child("stock")
                    .setValue(stock - selectedQty)

                Toast.makeText(this, "Order placed", Toast.LENGTH_SHORT).show()

                startActivity(Intent(this, UserOrdersActivity::class.java))
                finish()
            }
    }

    private fun loadName(node: String, id: String, tv: TextView) {
        FirebaseDatabase.getInstance().reference
            .child(node).child(id)
            .get()
            .addOnSuccessListener {
                tv.text = it.value?.toString() ?: ""
            }
    }
}
