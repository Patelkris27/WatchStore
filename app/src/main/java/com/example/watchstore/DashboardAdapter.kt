package com.example.watchstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DashboardAdapter(
    private val list: List<DashboardItem>,
    private val click: (String) -> Unit
) : RecyclerView.Adapter<DashboardAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val icon: ImageView = v.findViewById(R.id.imgIcon)
        val title: TextView = v.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_dashboard, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.icon.setImageResource(item.icon)
        holder.title.text = item.title
        holder.itemView.setOnClickListener { click(item.title) }
    }

    override fun getItemCount(): Int = list.size
}
