package com.example.watchstore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SkeletonAdapter : RecyclerView.Adapter<SkeletonAdapter.VH>() {

    class VH(v: android.view.View) : RecyclerView.ViewHolder(v)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_skeleton, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {}
    override fun getItemCount() = 6
}
