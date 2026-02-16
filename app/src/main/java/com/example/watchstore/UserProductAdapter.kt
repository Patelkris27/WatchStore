package com.example.watchstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class UserProductAdapter(
    private var list: List<Product>
) : RecyclerView.Adapter<UserProductAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgProduct)
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvPrice: TextView = v.findViewById(R.id.tvPrice)
        val tvStock: TextView = v.findViewById(R.id.tvStock)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val p = list[position]

        holder.tvName.text = p.name
        holder.tvPrice.text = "₹${p.price}"

        Glide.with(holder.itemView.context)
            .load(p.imageUrl)
            .into(holder.img)

        holder.tvStock.text = when {
            p.stock == 0 -> "Out of Stock"
            p.stock <= 5 -> "Low Stock"
            else -> ""
        }
        holder.itemView.setOnClickListener {
            val i = android.content.Intent(
                holder.itemView.context,
                ProductDetailsActivity::class.java
            )
            i.putExtra("id", p.id)
            holder.itemView.context.startActivity(i)
        }
    }

    override fun getItemCount(): Int = list.size

    fun filterList(filteredList: List<Product>) {
        list = filteredList
        notifyDataSetChanged()
    }
}
