package com.example.watchstore

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class UserOrderAdapter(
    private val list: List<Order>
) : RecyclerView.Adapter<UserOrderAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvProduct: TextView = v.findViewById(R.id.tvProduct)
        val tvQty: TextView = v.findViewById(R.id.tvQty)
        val tvTotal: TextView = v.findViewById(R.id.tvTotal)
        val tvStatus: TextView = v.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_order, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val o = list[position]

        holder.tvQty.text = "Qty: ${o.quantity}"
        holder.tvTotal.text = "Total: ₹${o.total}"
        holder.tvStatus.text = o.status

        holder.tvStatus.setTextColor(
            when (o.status) {
                "Delivered" -> Color.GREEN
                "Cancelled" -> Color.RED
                else -> Color.parseColor("#FFA000")
            }
        )

        FirebaseDatabase.getInstance().reference
            .child("products").child(o.productId)
            .get()
            .addOnSuccessListener {
                holder.tvProduct.text =
                    it.child("name").value?.toString() ?: ""
            }
    }

    override fun getItemCount(): Int = list.size
}
