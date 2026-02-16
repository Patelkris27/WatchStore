package com.example.watchstore

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var rgPayment: RadioGroup
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnPlaceOrder: Button
    private var totalAmount = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)

        etName = findViewById(R.id.etName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        rgPayment = findViewById(R.id.rgPayment)
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val cartRef = FirebaseDatabase.getInstance().reference
            .child("carts")
            .child(uid)

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                totalAmount = 0.0
                for (s in snapshot.children) {
                    val qty = s.child("quantity").getValue(Long::class.java) ?: 0L
                    val price = s.child("price").getValue(Double::class.java) ?: 0.0
                    totalAmount += qty * price
                }
                tvTotalAmount.text = "Total: ₹$totalAmount"
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        btnPlaceOrder.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (rgPayment.checkedRadioButtonId) {
                R.id.rbCod -> placeOrder(uid, name, phone, address, "COD")
                R.id.rbOnline -> showOnlinePaymentDialog(uid, name, phone, address)
            }
        }
    }

    private fun showOnlinePaymentDialog(
        uid: String,
        name: String,
        phone: String,
        address: String
    ) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_online_payment, null)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Online Payment")
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnGpay).setOnClickListener {
            placeOrder(uid, name, phone, address, "Google Pay")
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCreditCard).setOnClickListener {
            placeOrder(uid, name, phone, address, "Credit Card")
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnWallet).setOnClickListener {
            placeOrder(uid, name, phone, address, "Wallet")
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun placeOrder(
        uid: String,
        name: String,
        phone: String,
        address: String,
        paymentMethod: String
    ) {
        val db = FirebaseDatabase.getInstance().reference
        val cartRef = db.child("carts").child(uid)

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(this@CheckoutActivity, "Cart is empty", Toast.LENGTH_SHORT).show()
                    return
                }

                val cartItems = snapshot.children.mapNotNull { it.getValue(CartItem::class.java) }
                val productDetailsToFetch = cartItems.size
                var productDetailsFetched = 0
                val productDetails = mutableMapOf<String, Product>()
                val errors = mutableListOf<String>()

                for (cartItem in cartItems) {
                    db.child("products").child(cartItem.productId)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(productSnapshot: DataSnapshot) {
                                val product = productSnapshot.getValue(Product::class.java)
                                if (product != null) {
                                    if (product.stock < cartItem.quantity) {
                                        errors.add("Insufficient stock for ${product.name}")
                                    }
                                    productDetails[cartItem.productId] = product.copy(id = productSnapshot.key!!)
                                } else {
                                    errors.add("Product ${cartItem.productId} not found")
                                }

                                productDetailsFetched++

                                if (productDetailsFetched == productDetailsToFetch) {
                                    if (errors.isNotEmpty()) {
                                        Toast.makeText(this@CheckoutActivity, errors.joinToString(), Toast.LENGTH_LONG).show()
                                        return
                                    }

                                    // All checks passed, proceed to place order
                                    val orderId = db.child("orders").push().key!!
                                    val childUpdates = mutableMapOf<String, Any>()
                                    var total = 0.0

                                    for (item in cartItems) {
                                        val p = productDetails[item.productId]!!
                                        val newStock = p.stock - item.quantity
                                        childUpdates["/products/${item.productId}/stock"] = newStock
                                        total += item.price * item.quantity
                                    }

                                    val order = Order(
                                        orderId = orderId,
                                        userId = uid,
                                        products = cartItems,
                                        totalPrice = total,
                                        status = "Pending",
                                        paymentMethod = paymentMethod,
                                        name = name,
                                        phone = phone,
                                        address = address
                                    )
                                    childUpdates["/orders/$orderId"] = order

                                    db.updateChildren(childUpdates).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            cartRef.removeValue()
                                            Toast.makeText(this@CheckoutActivity, "Order placed successfully", Toast.LENGTH_SHORT).show()
                                            finish()
                                        } else {
                                            Toast.makeText(this@CheckoutActivity, "Failed to place order", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                errors.add("Failed to fetch product details for ${cartItem.productId}")
                                productDetailsFetched++
                                if (productDetailsFetched == productDetailsToFetch) {
                                     Toast.makeText(this@CheckoutActivity, errors.joinToString(), Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CheckoutActivity, "Failed to read cart", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
