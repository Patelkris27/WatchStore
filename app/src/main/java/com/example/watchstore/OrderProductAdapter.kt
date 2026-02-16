package com.example.watchstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OrderProductAdapter(
    private val products: List<Product>
) : RecyclerView.Adapter<OrderProductAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val ivProduct: ImageView = v.findViewById(R.id.ivProduct)
        val tvProductName: TextView = v.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = v.findViewById(R.id.tvProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_order_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val product = products[position]

        Glide.with(holder.itemView.context).load(product.imageUrl).into(holder.ivProduct)
        holder.tvProductName.text = product.name
        holder.tvProductPrice.text = "$${product.price}"
    }

    override fun getItemCount(): Int = products.size
}
