package com.example.watchstore

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class OrderAdapter(
    private val list: List<Order>
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private val db = FirebaseDatabase.getInstance().reference


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvOrderId: TextView = v.findViewById(R.id.tvOrderId)
        val tvTotal: TextView = v.findViewById(R.id.tvTotal)
        val tvStatus: TextView = v.findViewById(R.id.tvStatus)
        val tvName : TextView = v.findViewById(R.id.tvName)
        val btnUpdate: Button = v.findViewById(R.id.btnUpdateStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = list[position]

        holder.tvOrderId.text = "Order ID: ${order.id}"
        holder.tvName.text = "UserName: ${order.user}"
        holder.tvTotal.text = "Total: ₹${order.total}"
        holder.tvStatus.text = "Status: ${order.status}"

        holder.btnUpdate.setOnClickListener {
            val statuses = arrayOf("Pending", "Shipped", "Delivered")

            AlertDialog.Builder(holder.itemView.context)
                .setIcon(R.drawable.logob)
                .setItems(statuses) { _, which ->
                    db.child(order.id).child("status").setValue(statuses[which])
                }
                .show()
        }
    }

    override fun getItemCount(): Int = list.size
}
