package com.example.watchstore

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var rgPayment: RadioGroup
    private lateinit var tvTotalAmount: TextView
    private lateinit var btnPlaceOrder: Button
    private var totalAmount = 0

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
        val cartRef = FirebaseDatabase.getInstance().reference.child("carts").child(uid)

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (s in snapshot.children) {
                    val qty = s.child("quantity").getValue(Int::class.java) ?: 0
                    val price = s.child("price").getValue(Int::class.java) ?: 0
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
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            when (rgPayment.checkedRadioButtonId) {
                R.id.rbCod -> placeOrder(uid, name, phone, address, "COD")
                R.id.rbOnline -> showOnlinePaymentDialog(uid, name, phone, address)
            }
        }
    }

    private fun showOnlinePaymentDialog(uid: String, name: String, phone: String, address: String) {
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

    private fun placeOrder(uid: String, name: String, phone: String, address: String, paymentMethod: String) {
        val ordersRef = FirebaseDatabase.getInstance().reference.child("orders")
        val cartRef = FirebaseDatabase.getInstance().reference.child("carts").child(uid)
        val orderId = ordersRef.push().key!!

        cartRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (itemSnapshot in snapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItem::class.java)
                    if (cartItem != null) {
                        val order = Order(
                            id = orderId,
                            productId = cartItem.productId,
                            quantity = cartItem.quantity,
                            total = cartItem.price * cartItem.quantity,
                            date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date()),
                            status = "Pending",
                            user = FirebaseAuth.getInstance().currentUser?.email ?: "",
                            paymentMethod = paymentMethod,
                            address = address,
                            name = name,
                            phone = phone
                        )
                        ordersRef.child(orderId).setValue(order)
                    }
                }

                // Clear the cart
                cartRef.removeValue()

                Toast.makeText(this@CheckoutActivity, "Order placed successfully", Toast.LENGTH_SHORT).show()
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@CheckoutActivity, "Failed to place order", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
