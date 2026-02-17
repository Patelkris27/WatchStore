package com.example.watchstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OrderDetailAdapter(
    private val list: List<Product>
) : RecyclerView.Adapter<OrderDetailAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val ivProductImage: ImageView = v.findViewById(R.id.ivProductImage)
        val tvProductName: TextView = v.findViewById(R.id.tvProductName)
        val tvProductQty: TextView = v.findViewById(R.id.tvProductQty)
        val tvProductPrice: TextView = v.findViewById(R.id.tvProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_detail, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = list[position]

        holder.tvProductName.text = p.name
        holder.tvProductQty.text = "Qty: ${p.quantity}"
        holder.tvProductPrice.text = "Price: ${p.price}"

        Glide.with(holder.itemView.context)
            .load(p.imageUrl)
            .into(holder.ivProductImage)
    }

    override fun getItemCount(): Int = list.size
}
