package com.example.watchstore

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.watchstore.utils.DialogUtil
import com.google.firebase.database.*

class ProductAdapter(
    private val list: List<Product>,
    private val rootDb: DatabaseReference
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgProduct)
        val tvName: TextView = v.findViewById(R.id.tvProduct)
        val tvMeta: TextView = v.findViewById(R.id.tvMeta)
        val tvPrice: TextView = v.findViewById(R.id.tvPrice)
        val tvLowStock: TextView = v.findViewById(R.id.tvLowStock)
        val btnEdit: Button = v.findViewById(R.id.btnEdit)
        val btnDelete: Button = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_product, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val p = list[position]

        holder.tvName.text = p.name
        holder.tvPrice.text = "₹${p.price}"

        Glide.with(holder.itemView.context).load(p.imageUrl).into(holder.img)

        rootDb.child("brands").child(p.brandId).get().addOnSuccessListener { b ->
            rootDb.child("categories").child(p.categoryId).get().addOnSuccessListener { c ->
                holder.tvMeta.text = "${b.value}\n${c.value}"
            }
        }

        holder.tvLowStock.visibility =
            if (p.stock <= 5) View.VISIBLE else View.GONE

        holder.btnEdit.setOnClickListener {
            openEditDialog(holder.itemView.context, p)
        }

        holder.btnDelete.setOnClickListener {
            DialogUtil.showDeleteDialog(
                holder.itemView.context,
                "Delete Product",
                "This product will be deleted"
            ) {
                rootDb.child("products").child(p.id).removeValue()
            }
        }
    }

    override fun getItemCount(): Int = list.size

    private fun openEditDialog(context: android.content.Context, p: Product) {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 10)

        val etName = EditText(context)
        etName.setText(p.name)

        val etPrice = EditText(context)
        etPrice.setText(p.price)
        etPrice.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        val etImage = EditText(context)
        etImage.setText(p.imageUrl)

        val etStock = EditText(context)
        etStock.setText(p.stock.toString())
        etStock.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        val spBrand = Spinner(context)
        val spCategory = Spinner(context)

        layout.addView(etName)
        layout.addView(etPrice)
        layout.addView(etImage)
        layout.addView(etStock)
        layout.addView(spBrand)
        layout.addView(spCategory)

        val brandMap = HashMap<String, String>()
        val categoryMap = HashMap<String, String>()

        loadSpinner(rootDb.child("brands"), spBrand, brandMap, p.brandId)
        loadSpinner(rootDb.child("categories"), spCategory, categoryMap, p.categoryId)

        AlertDialog.Builder(context)
            .setIcon(R.drawable.logob)
            .setTitle("Edit Product")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                rootDb.child("products").child(p.id).setValue(
                    mapOf(
                        "name" to etName.text.toString(),
                        "price" to etPrice.text.toString(),
                        "imageUrl" to etImage.text.toString(),
                        "brandId" to brandMap[spBrand.selectedItem.toString()]!!,
                        "categoryId" to categoryMap[spCategory.selectedItem.toString()]!!,
                        "stock" to etStock.text.toString().toInt()
                    )
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadSpinner(
        ref: DatabaseReference,
        spinner: Spinner,
        map: HashMap<String, String>,
        selectedId: String
    ) {
        ref.get().addOnSuccessListener {
            val list = ArrayList<String>()
            var index = 0
            var selectedIndex = 0

            for (s in it.children) {
                val name = s.value.toString()
                map[name] = s.key!!
                list.add(name)
                if (s.key == selectedId) selectedIndex = index
                index++
            }

            spinner.adapter = ArrayAdapter(
                spinner.context,
                android.R.layout.simple_spinner_dropdown_item,
                list
            )
            spinner.setSelection(selectedIndex)
        }
    }
}
