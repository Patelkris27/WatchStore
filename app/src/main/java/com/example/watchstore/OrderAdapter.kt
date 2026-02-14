package com.example.watchstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
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
        fun bind(order: Order) {
            binding.tvOrderId.text = "Order ID: ${order.orderId}"
            binding.tvTotal.text = "Total: Rs${order.totalPrice}"
            binding.tvStatus.text = "Status: ${order.status}"

            FirebaseDatabase.getInstance().reference.child("users").child(order.userId).get().addOnSuccessListener {
                binding.tvName.text = it.child("name").value?.toString() ?: ""
            }

            val statuses = itemView.context.resources.getStringArray(R.array.order_status_array)
            val adapter = ArrayAdapter(itemView.context, android.R.layout.simple_spinner_item, statuses)
            binding.spinnerStatus.adapter = adapter
            binding.spinnerStatus.setSelection(statuses.indexOf(order.status))

            binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val newStatus = statuses[position]
                    if (newStatus != order.status) {
                        FirebaseDatabase.getInstance().reference.child("orders").child(order.orderId).child("status").setValue(newStatus)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }
}
