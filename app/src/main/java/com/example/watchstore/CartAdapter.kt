package com.example.watchstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.play.core.integrity.p
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class CartAdapter(
    private val list: List<CartItem>
) : RecyclerView.Adapter<CartAdapter.VH>() {

    private val uid = FirebaseAuth.getInstance().currentUser!!.uid
    private val db = FirebaseDatabase.getInstance().reference
        .child("carts").child(uid)

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvName: TextView = v.findViewById(R.id.tvName)
        val tvQty: TextView = v.findViewById(R.id.tvQty)
        val img: ImageView = v.findViewById(R.id.imgProduct)
        val tvPrice: TextView = v.findViewById(R.id.tvPrice)
        val btnPlus: Button = v.findViewById(R.id.btnPlus)
        val btnMinus: Button = v.findViewById(R.id.btnMinus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_cart, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val c = list[position]

        holder.tvQty.text = c.quantity.toString()
        holder.tvPrice.text = "₹${c.quantity * c.price}"

        Glide.with(holder.itemView.context)
            .load(c.imageUrl)
            .into(holder.img)

        FirebaseDatabase.getInstance().reference
            .child("products").child(c.productId)
            .get()
            .addOnSuccessListener {
                holder.tvName.text = it.child("name").value.toString()
            }

        holder.btnPlus.setOnClickListener {
            db.child(c.productId)
                .child("quantity")
                .setValue(c.quantity + 1)
        }

        holder.btnMinus.setOnClickListener {
            if (c.quantity > 1) {
                db.child(c.productId)
                    .child("quantity")
                    .setValue(c.quantity - 1)
            } else {
                db.child(c.productId).removeValue()
            }
        }
    }

    override fun getItemCount(): Int = list.size
}
