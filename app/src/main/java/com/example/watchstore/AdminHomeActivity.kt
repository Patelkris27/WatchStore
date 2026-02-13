package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.watchstore.databinding.ActivityHomeAdminBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeAdminBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var ordersValueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference

        setupViews()
        loadAnalytics()
        setupDashboard()
    }

    private fun setupViews() {
        binding.tvAdminName.text = getString(R.string.admin_name)
        binding.tvAdminEmail.text = auth.currentUser?.email ?: ""
        binding.rvDashboard.layoutManager = GridLayoutManager(this, 2)
    }

    private fun loadAnalytics() {
        db.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvTotalUsers.text = getString(R.string.total_users_format, snapshot.childrenCount)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        ordersValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvTotalOrders.text = getString(R.string.total_orders_format, snapshot.childrenCount)

                var revenue = 0
                var todayCount = 0
                val today = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())

                for (s in snapshot.children) {
                    val total = s.child("total").getValue(Int::class.java) ?: 0
                    val date = s.child("date").getValue(String::class.java)

                    revenue += total
                    if (date == today) todayCount++
                }

                binding.tvRevenue.text = getString(R.string.total_revenue_format, revenue.toString())
                binding.tvTodayOrders.text = getString(R.string.today_orders_format, todayCount)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        db.child("orders").addValueEventListener(ordersValueEventListener)
    }

    private fun setupDashboard() {
        val items = listOf(
            DashboardItem(getString(R.string.brands), R.drawable.ic_brand),
            DashboardItem(getString(R.string.categories), R.drawable.ic_category),
            DashboardItem(getString(R.string.products), R.drawable.ic_product),
            DashboardItem(getString(R.string.orders), R.drawable.ic_orders),
            DashboardItem(getString(R.string.users), R.drawable.ic_users),
            DashboardItem(getString(R.string.settings), R.drawable.ic_settings)
        )

        binding.rvDashboard.adapter = DashboardAdapter(items) { title ->
            when (title) {
                getString(R.string.brands) -> startActivity(Intent(this, ManageBrandsActivity::class.java))
                getString(R.string.categories) -> startActivity(Intent(this, ManageCategoriesActivity::class.java))
                getString(R.string.products) -> startActivity(Intent(this, ManageProductsActivity::class.java))
                getString(R.string.orders) -> startActivity(Intent(this, ManageOrdersActivity::class.java))
                getString(R.string.users) -> startActivity(Intent(this, ManageUsersActivity::class.java))
                getString(R.string.settings) -> startActivity(Intent(this, AdminSettingsActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.child("orders").removeEventListener(ordersValueEventListener)
    }
}
