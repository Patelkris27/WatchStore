package com.example.watchstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class OrderProductAdapter(
    private val products: List<Product>
) : RecyclerView.Adapter<OrderProductAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val ivProduct: ImageView = v.findViewById(R.id.ivProductImage)
        val tvProductName: TextView = v.findViewById(R.id.tvProductName)
        val tvProductPrice: TextView = v.findViewById(R.id.tvProductPrice)
        val tvQuantity: TextView = v.findViewById(R.id.tvQuantity)
        val llQuantity: LinearLayout = v.findViewById(R.id.llQuantity)
        val btnCancel: Button = v.findViewById(R.id.btnCancel)
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
        holder.tvProductPrice.text = "₹${product.price}"
        holder.tvQuantity.text = product.quantity.toString()

        // Hide edit controls for admin view (unless you want them)
        holder.llQuantity.findViewById<View>(R.id.ivDecreaseQuantity).visibility = View.GONE
        holder.llQuantity.findViewById<View>(R.id.ivIncreaseQuantity).visibility = View.GONE
        holder.btnCancel.visibility = View.GONE
    }

    override fun getItemCount(): Int = products.size
}
