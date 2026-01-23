package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_admin)
        val tvUsers = findViewById<TextView>(R.id.tvTotalUsers)
        val tvOrders = findViewById<TextView>(R.id.tvTotalOrders)
        val tvRevenue = findViewById<TextView>(R.id.tvRevenue)
        val tvToday = findViewById<TextView>(R.id.tvTodayOrders)

        val db = FirebaseDatabase.getInstance().reference

        db.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tvUsers.text = "Users: ${snapshot.childrenCount}"
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        db.child("orders").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                tvOrders.text = "Orders: ${snapshot.childrenCount}"

                var revenue = 0
                var todayCount = 0
                val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

                for (s in snapshot.children) {
                    val total = s.child("total").getValue(Int::class.java) ?: 0
                    val date = s.child("date").getValue(String::class.java)

                    revenue += total
                    if (date == today) todayCount++
                }

                tvRevenue.text = "Revenue: ₹$revenue"
                tvToday.text = "Today: $todayCount"
            }

            override fun onCancelled(error: DatabaseError) {}
        })


        auth = FirebaseAuth.getInstance()

        val rvDashboard = findViewById<RecyclerView>(R.id.rvDashboard)
        val tvAdminName = findViewById<TextView>(R.id.tvAdminName)
        val tvAdminEmail = findViewById<TextView>(R.id.tvAdminEmail)

        tvAdminName.text = "Admin"
        tvAdminEmail.text = auth.currentUser?.email ?: ""

        rvDashboard.layoutManager = GridLayoutManager(this, 2)

        val items = listOf(
            DashboardItem("Brands", R.drawable.ic_brand),
            DashboardItem("Categories", R.drawable.ic_category),
            DashboardItem("Products", R.drawable.ic_product),
            DashboardItem("Orders", R.drawable.ic_orders),
            DashboardItem("Users", R.drawable.ic_users),
            DashboardItem("Settings",R.drawable.ic_settings)
        )

        rvDashboard.adapter = DashboardAdapter(items) { title ->
            when (title) {
                "Brands" -> startActivity(Intent(this, ManageBrandsActivity::class.java))
                "Categories" -> startActivity(Intent(this, ManageCategoriesActivity::class.java))
                "Products" -> startActivity(Intent(this, ManageProductsActivity::class.java))
                "Orders" -> startActivity(Intent(this, ManageOrdersActivity::class.java))
                "Users" -> startActivity(Intent(this, ManageUsersActivity::class.java))
                "Settings" -> startActivity(Intent(this,AdminSettingsActivity::class.java))
            }
        }

    }

}
