package com.example.watchstore

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.data.*
import com.google.firebase.database.*
import com.google.android.material.button.MaterialButton
import com.example.watchstore.utils.ReportsUtil

class ManageOrdersActivity : AppCompatActivity() {

    private lateinit var db: DatabaseReference
    private val orderList = ArrayList<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_orders)

        val rvOrders = findViewById<RecyclerView>(R.id.rvOrders)
        val btnExport = findViewById<MaterialButton>(R.id.btnExport)
        val chartOrders = findViewById<BarChart>(R.id.chartOrders)
        val chartRevenue = findViewById<LineChart>(R.id.chartRevenue)

        rvOrders.layoutManager = LinearLayoutManager(this)

        db = FirebaseDatabase.getInstance().reference.child("orders")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                orderList.clear()

                val orderEntries = ArrayList<com.github.mikephil.charting.data.BarEntry>()
                val revenueEntries = ArrayList<com.github.mikephil.charting.data.Entry>()

                var index = 0

                for (s in snapshot.children) {

                    val order = Order(
                        id = s.key ?: "",
                        productId = s.child("productId").value.toString(),
                        quantity = s.child("quantity").getValue(Int::class.java) ?: 0,
                        total = s.child("total").getValue(Int::class.java) ?: 0,
                        date = s.child("date").value.toString(),
                        status = s.child("status").value?.toString() ?: "Pending"
                    )

                    orderList.add(order)

                    orderEntries.add(
                        com.github.mikephil.charting.data.BarEntry(
                            index.toFloat(),
                            1f
                        )
                    )

                    revenueEntries.add(
                        com.github.mikephil.charting.data.Entry(
                            index.toFloat(),
                            order.total.toFloat()
                        )
                    )

                    index++
                }

                rvOrders.adapter = OrderAdapter(orderList)

                setupCharts(chartOrders, chartRevenue, orderEntries, revenueEntries)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        btnExport.setOnClickListener {
            ReportsUtil.exportOrders(this)
            Toast.makeText(this, "Orders report exported", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupCharts(
        barChart: BarChart,
        lineChart: LineChart,
        orderEntries: List<BarEntry>,
        revenueEntries: List<Entry>
    ) {

        barChart.data = BarData(
            BarDataSet(orderEntries, "Orders").apply {
                color = getColor(R.color.theme)
            }
        )
        barChart.invalidate()

        lineChart.data = LineData(
            LineDataSet(revenueEntries, "Revenue").apply {
                color = getColor(R.color.theme)
            }
        )
        lineChart.invalidate()
    }
}
