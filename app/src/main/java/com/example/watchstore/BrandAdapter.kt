package com.example.watchstore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.watchstore.utils.DialogUtil
import com.google.firebase.database.FirebaseDatabase

class BrandAdapter(
    private val list: List<Brand>
) : RecyclerView.Adapter<BrandAdapter.VH>() {

    private val db = FirebaseDatabase.getInstance().reference.child("brands")

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvBrand)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_brand, parent, false))
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val brand = list[position]
        holder.tvName.text = brand.name

        holder.itemView.setOnLongClickListener {
            val popup = PopupMenu(holder.itemView.context, it)
            popup.menu.add("Edit")
            popup.menu.add("Delete")
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Edit" -> showEditDialog(holder.itemView.context, brand)
                    "Delete" -> {
                        DialogUtil.showDeleteDialog(
                            holder.itemView.context,
                            "Delete Brand",
                            "This will remove the brand permanently"
                        ) {
                            db.child(brand.id).removeValue()
                        }
                    }
                }
                true
            }
            popup.show()
            true
        }
    }

    override fun getItemCount() = list.size

    private fun showEditDialog(context: Context, brand: Brand) {
        val et = EditText(context)
        et.setText(brand.name)

        AlertDialog.Builder(context)
            .setIcon(R.drawable.logob)
            .setTitle("Edit Brand")
            .setView(et)
            .setPositiveButton("Update") { _, _ ->
                db.child(brand.id).setValue(et.text.toString())
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
