package com.example.watchstore

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.watchstore.databinding.ItemOrderProductBinding
import com.google.firebase.database.FirebaseDatabase

class OrderDetailsAdapter(
    private val context: Context,
    private val productList: MutableList<Product>,
    private val orderId: String
) : RecyclerView.Adapter<OrderDetailsAdapter.OrderDetailsViewHolder>() {

    private val db = FirebaseDatabase.getInstance().reference.child("orders")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding = ItemOrderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        val product = productList[position]
        holder.binding.tvProductName.text = product.name
        holder.binding.tvProductPrice.text = "Price: ₹${product.price}"
        holder.binding.tvQuantity.text = product.quantity.toString()

        Glide.with(context)
            .load(product.imageUrl)
            .into(holder.binding.ivProductImage)

        holder.binding.btnCancel.setOnClickListener {
            productList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, productList.size)
            updateDatabase()
        }

        holder.binding.ivDecreaseQuantity.setOnClickListener {
            if (product.quantity > 1) {
                product.quantity--
                holder.binding.tvQuantity.text = product.quantity.toString()
                updateDatabase()
            }
        }

        holder.binding.ivIncreaseQuantity.setOnClickListener {
            product.quantity++
            holder.binding.tvQuantity.text = product.quantity.toString()
            updateDatabase()
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    private fun updateDatabase() {
        val updatedProducts = productList.map {
            CartItem(it.productId, it.name, it.quantity.toLong(), it.image, it.price)
        }
        db.child(orderId).child("products").setValue(updatedProducts)
    }

    inner class OrderDetailsViewHolder(val binding: ItemOrderProductBinding) : RecyclerView.ViewHolder(binding.root)
}
