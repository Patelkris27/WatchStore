package com.example.watchstore

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.watchstore.databinding.ItemOrderBinding
import com.google.firebase.database.FirebaseDatabase

class OrderAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.bind(order)
    }

    override fun getItemCount(): Int = orders.size

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val order = orders[position]
                    val intent = Intent(itemView.context, AdminOrderDetailsActivity::class.java)
                    intent.putExtra("orderId", order.orderId)
                    itemView.context.startActivity(intent)
                }
            }
        }

        fun bind(order: Order) {
            binding.tvOrderId.text = "Order ID: ${order.orderId}"
            binding.tvTotal.text = "Total: Rs${order.totalPrice}"
            binding.tvStatus.text = "Status: ${order.status}"

            FirebaseDatabase.getInstance().reference.child("users").child(order.userId).get().addOnSuccessListener {
                binding.tvName.text = it.child("name").value?.toString() ?: ""
            }

            val statuses = itemView.context.resources.getStringArray(R.array.order_status_array)
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_dropdown_item_1line, statuses)
            binding.actStatus.setAdapter(adapter)
            binding.actStatus.setText(order.status, false)

            binding.actStatus.setOnItemClickListener { _, _, position, _ ->
                val selectedStatus = statuses[position]
                FirebaseDatabase.getInstance().reference.child("orders").child(order.orderId).child("status").setValue(selectedStatus)
                setStatusColor(this, selectedStatus)
            }
            setStatusColor(this, order.status)
        }
    }

    private fun setStatusColor(holder: OrderViewHolder, status: String) {
        when (status) {
            "Pending" -> holder.itemView.findViewById<android.widget.TextView>(R.id.tvStatus).setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.holo_orange_dark)
            )
            "Processing" -> holder.itemView.findViewById<android.widget.TextView>(R.id.tvStatus).setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.holo_blue_dark)
            )
            "Shipped" -> holder.itemView.findViewById<android.widget.TextView>(R.id.tvStatus).setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.holo_purple)
            )
            "Delivered" -> holder.itemView.findViewById<android.widget.TextView>(R.id.tvStatus).setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.holo_green_dark)
            )
        }
    }
}
