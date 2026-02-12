package com.example.watchstore

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class OrderAdapter(
    private val list: List<Order>
) : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private val db = FirebaseDatabase.getInstance().reference.child("orders")


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvOrderId: TextView = v.findViewById(R.id.tvOrderId)
        val tvTotal: TextView = v.findViewById(R.id.tvTotal)
        val tvStatus: TextView = v.findViewById(R.id.tvStatus)
        val tvName : TextView = v.findViewById(R.id.tvName)
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

        holder.itemView.setOnLongClickListener {
            val statuses = arrayOf("Pending", "Shipped", "Delivered", "Cancelled")
            val popup = PopupMenu(holder.itemView.context, it)
            for (status in statuses) {
                popup.menu.add(status)
            }
            popup.setOnMenuItemClickListener { item ->
                db.child(order.id).child("status").setValue(item.title.toString())
                true
            }
            popup.show()
            true
        }
    }

    override fun getItemCount(): Int = list.size
}
