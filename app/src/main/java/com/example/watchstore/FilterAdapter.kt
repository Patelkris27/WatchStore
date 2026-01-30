package com.example.watchstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FilterAdapter(
    private val list: List<Pair<String, String>>,
    private val onClick: (String?) -> Unit
) : RecyclerView.Adapter<FilterAdapter.VH>() {

    private var selected = 0

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tv: TextView = v.findViewById(R.id.tvChip)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_filter_chip, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.tv.text = list[position].first
        holder.tv.isSelected = position == selected

        holder.tv.setOnClickListener {
            selected = position
            notifyDataSetChanged()
            onClick(list[position].second)
        }
    }

    override fun getItemCount(): Int = list.size
}
