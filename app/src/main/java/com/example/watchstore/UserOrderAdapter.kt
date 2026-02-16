
package com.example.watchstore

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watchstore.OrderDetailsActivity
import com.example.watchstore.utils.DialogUtil
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UserOrderAdapter(
    private val list: List<Order>
) : RecyclerView.Adapter<UserOrderAdapter.VH>() {

    private val db = FirebaseDatabase.getInstance().reference.child("orders")

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvDate: TextView = v.findViewById(R.id.tvDate)
        val tvStatus: TextView = v.findViewById(R.id.tvStatus)
        val btnCancel: Button = v.findViewById(R.id.btnCancel)
        val llOrder: LinearLayout = v.findViewById(R.id.llOrder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_order, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val o = list[position]

        holder.tvDate.text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(o.orderDate))
        holder.tvStatus.text = o.status

        holder.tvStatus.setTextColor(
            when (o.status) {
                "Delivered" -> Color.GREEN
                "Cancelled" -> Color.RED
                else -> Color.parseColor("#FFA000")
            }
        )

        holder.btnCancel.visibility =
            if (o.status == "Delivered" || o.status == "Cancelled")
                View.GONE
            else
                View.VISIBLE

        holder.btnCancel.setOnClickListener {
            DialogUtil.showDeleteDialog(
                holder.itemView.context,
                "Cancel Order",
                "Are you sure you want to cancel this order?"
            ) {
                db.child(o.orderId).child("status").setValue("Cancelled")
            }
        }

        holder.llOrder.setOnClickListener {
            val intent = Intent(holder.itemView.context, OrderDetailsActivity::class.java)
            for (product in o.products) {
                // No need to do anything here, just iterating
            }
            holder.itemView.context.startActivity(intent)
        }
    }


    override fun getItemCount(): Int = list.size
}
