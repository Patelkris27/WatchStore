package com.example.watchstore

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watchstore.utils.DialogUtil
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CategoryAdapter(
    private val list: List<Category>,
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private val db = FirebaseDatabase.getInstance().reference.child("categories")


    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvCategory: TextView = v.findViewById(R.id.tvCategory)
        val btnEdit: Button = v.findViewById(R.id.btnEdit)
        val btnDelete: Button = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = list[position]
        holder.tvCategory.text = category.name

            holder.btnEdit.setOnClickListener {
                val input = EditText(holder.itemView.context)
                input.setText(category.name)

                AlertDialog.Builder(holder.itemView.context)
                    .setIcon(R.drawable.logob)
                    .setTitle("Update Category")
                    .setView(input)
                    .setPositiveButton("Update") { _, _ ->
                        db.child(category.id).setValue(input.text.toString())
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        holder.btnDelete.setOnClickListener {
            DialogUtil.showDeleteDialog(
                holder.itemView.context,
                "Delete Category",
                "This will remove the category permanently"
            ) {
                db.child(category.id).removeValue()
            }
        }
    }
    override fun getItemCount(): Int = list.size
}
