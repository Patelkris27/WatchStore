package com.example.watchstore

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.watchstore.utils.DialogUtil
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class UserAdapter(
    private val list: List<User>
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private val db: DatabaseReference =
        FirebaseDatabase.getInstance().reference.child("users")

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvUserName)
        val tvEmail: TextView = v.findViewById(R.id.tvUserEmail)
        val btnDelete: Button = v.findViewById(R.id.btnDeleteUser)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_user, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = list[position]

        holder.tvName.text = user.name
        holder.tvEmail.text = user.email

        holder.btnDelete.setOnClickListener {
            DialogUtil.showDeleteDialog(
                holder.itemView.context,
                "Delete User",
                "User data will be removed"
            ) {
                db.child(user.id).removeValue()
            }
        }

    }

    override fun getItemCount(): Int = list.size
}
