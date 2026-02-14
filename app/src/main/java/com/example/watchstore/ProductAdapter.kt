package com.example.watchstore

import android.app.AlertDialog
import android.content.Context
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.watchstore.utils.DialogUtil
import com.google.firebase.database.DatabaseReference

class ProductAdapter(
    private val list: List<Product>,
    private val rootDb: DatabaseReference,
    private val brandsMap: Map<String, String>,
    private val categoriesMap: Map<String, String>
) : RecyclerView.Adapter<ProductAdapter.ViewHolder>() {

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val img: ImageView = v.findViewById(R.id.imgProduct)
        val tvName: TextView = v.findViewById(R.id.tvProduct)
        val tvMeta: TextView = v.findViewById(R.id.tvMeta)
        val tvPrice: TextView = v.findViewById(R.id.tvPrice)
        val tvLowStock: TextView = v.findViewById(R.id.tvLowStock)
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
        holder.tvMeta.text = "${brandsMap[p.brandId]}\n${categoriesMap[p.categoryId]}"

        Glide.with(holder.itemView.context).load(p.imageUrl).into(holder.img)

        holder.tvLowStock.visibility =
            if (p.stock <= 5) View.VISIBLE else View.GONE

        holder.itemView.setOnLongClickListener {
            val popup = PopupMenu(holder.itemView.context, it)
            popup.menu.add("Edit")
            popup.menu.add("Delete")
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Edit" -> openEditDialog(holder.itemView.context, p)
                    "Delete" -> {
                        DialogUtil.showDeleteDialog(
                            holder.itemView.context,
                            "Delete Product",
                            "This product will be deleted"
                        ) {
                            rootDb.child("products").child(p.id).removeValue()
                        }
                    }
                }
                true
            }
            popup.show()
            true
        }
    }

    override fun getItemCount(): Int = list.size

    private fun openEditDialog(context: Context, p: Product) {
        val layout = LinearLayout(context)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(40, 20, 40, 10)

        val etName = EditText(context)
        etName.setText(p.name)

        val etPrice = EditText(context)
        etPrice.setText(p.price.toString())
        etPrice.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL

        val etImage = EditText(context)
        etImage.setText(p.imageUrl)

        val etStock = EditText(context)
        etStock.setText(p.stock.toString())
        etStock.inputType = InputType.TYPE_CLASS_NUMBER

        val spBrand = Spinner(context)
        val spCategory = Spinner(context)

        layout.addView(etName)
        layout.addView(etPrice)
        layout.addView(etImage)
        layout.addView(etStock)
        layout.addView(spBrand)
        layout.addView(spCategory)

        val brandList = ArrayList(brandsMap.values)
        val brandAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, brandList)
        spBrand.adapter = brandAdapter
        val brandPosition = brandList.indexOf(brandsMap[p.brandId])
        if (brandPosition != -1) {
            spBrand.setSelection(brandPosition)
        }

        val categoryList = ArrayList(categoriesMap.values)
        val categoryAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, categoryList)
        spCategory.adapter = categoryAdapter
        val categoryPosition = categoryList.indexOf(categoriesMap[p.categoryId])
        if (categoryPosition != -1) {
            spCategory.setSelection(categoryPosition)
        }

        val brandNameToIdMap = brandsMap.entries.associateBy({ it.value }) { it.key }
        val categoryNameToIdMap = categoriesMap.entries.associateBy({ it.value }) { it.key }

        AlertDialog.Builder(context)
            .setIcon(R.drawable.logob)
            .setTitle("Edit Product")
            .setView(layout)
            .setPositiveButton("Update") { _, _ ->
                if (spBrand.selectedItem == null || spCategory.selectedItem == null) {
                    Toast.makeText(context, "Brand or category is not available.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val selectedBrandName = spBrand.selectedItem.toString()
                val selectedCategoryName = spCategory.selectedItem.toString()

                val selectedBrandId = brandNameToIdMap[selectedBrandName]
                val selectedCategoryId = categoryNameToIdMap[selectedCategoryName]

                if (selectedBrandId == null || selectedCategoryId == null) {
                    Toast.makeText(context, "Could not find brand or category ID.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                rootDb.child("products").child(p.id).setValue(
                    mapOf(
                        "name" to etName.text.toString(),
                        "price" to etPrice.text.toString().toDouble(),
                        "imageUrl" to etImage.text.toString(),
                        "brandId" to selectedBrandId,
                        "categoryId" to selectedCategoryId,
                        "stock" to etStock.text.toString().toInt()
                    )
                )
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
