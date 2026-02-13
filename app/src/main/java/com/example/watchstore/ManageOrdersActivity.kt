package com.example.watchstore

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityManageOrdersBinding
import com.example.watchstore.utils.ReportsUtil
import com.github.mikephil.charting.data.*
import com.google.firebase.database.*

class ManageOrdersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageOrdersBinding
    private lateinit var db: DatabaseReference
    private val orderList = ArrayList<Order>()
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference.child("orders")

        setupRecyclerView()
        setupClickListeners()
        loadOrders()
    }

    private fun setupRecyclerView() {
        binding.rvOrders.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        binding.btnExport.setOnClickListener {
            ReportsUtil.exportOrders(this)
            Toast.makeText(this, getString(R.string.orders_report_exported), Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadOrders() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                val orderEntries = ArrayList<BarEntry>()
                val revenueEntries = ArrayList<Entry>()
                var index = 0

                for (s in snapshot.children) {
                    val order = s.getValue(Order::class.java)
                    order?.let {
                        orderList.add(it)
                        orderEntries.add(BarEntry(index.toFloat(), 1f))
                        revenueEntries.add(Entry(index.toFloat(), it.total.toFloat()))
                        index++
                    }
                }

                binding.rvOrders.adapter = OrderAdapter(orderList)
                setupCharts(orderEntries, revenueEntries)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageOrdersActivity, "Failed to load orders", Toast.LENGTH_SHORT).show()
            }
        }
        db.addValueEventListener(valueEventListener)
    }

    private fun setupCharts(orderEntries: List<BarEntry>, revenueEntries: List<Entry>) {
        binding.chartOrders.data = BarData(
            BarDataSet(orderEntries, "Orders").apply {
                color = getColor(R.color.theme)
            }
        )
        binding.chartOrders.invalidate()

        binding.chartRevenue.data = LineData(
            LineDataSet(revenueEntries, "Revenue").apply {
                color = getColor(R.color.theme)
            }
        )
        binding.chartRevenue.invalidate()
    }

    override fun onDestroy() {
        super.onDestroy()
        db.removeEventListener(valueEventListener)
    }
}
