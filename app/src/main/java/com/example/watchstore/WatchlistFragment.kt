package com.example.watchstore

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.watchstore.databinding.FragmentWatchlistBinding
import com.google.firebase.database.*
import java.util.Locale

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    private val productList = ArrayList<Product>()
    private lateinit var db: DatabaseReference
    private lateinit var adapter: UserProductAdapter
    private lateinit var productsValueEventListener: ValueEventListener

    private val brandsMap = HashMap<String, String>()
    private val categoriesMap = HashMap<String, String>()

    private var brandsLoaded = false
    private var categoriesLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvWatchlist.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = UserProductAdapter(ArrayList())
        binding.rvWatchlist.adapter = adapter

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        loadInitialData()
    }

    private fun loadInitialData() {
        loadBrands()
        loadCategories()
    }

    private fun onLookupsLoaded() {
        if (brandsLoaded && categoriesLoaded) {
            loadProducts()
        }
    }

    private fun loadBrands() {
        val brandsRef = FirebaseDatabase.getInstance().reference.child("brands")
        brandsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (!isAdded || _binding == null) return

                brandsMap.clear()
                for (s in snapshot.children) {
                    val brandId = s.key ?: continue
                    val brandName = when {
                        s.value is String -> s.value.toString()
                        s.child("name").exists() -> s.child("name").value.toString()
                        else -> continue
                    }
                    brandsMap[brandId] = brandName
                }

                brandsLoaded = true
                onLookupsLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                brandsLoaded = true
                onLookupsLoaded()
            }
        })
    }

    private fun loadCategories() {
        val categoriesRef = FirebaseDatabase.getInstance().reference.child("categories")
        categoriesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (!isAdded || _binding == null) return

                categoriesMap.clear()
                for (s in snapshot.children) {
                    val categoryId = s.key ?: continue
                    val categoryName = when {
                        s.value is String -> s.value.toString()
                        s.child("name").exists() -> s.child("name").value.toString()
                        else -> continue
                    }
                    categoriesMap[categoryId] = categoryName
                }

                categoriesLoaded = true
                onLookupsLoaded()
            }

            override fun onCancelled(error: DatabaseError) {
                categoriesLoaded = true
                onLookupsLoaded()
            }
        })
    }

    private fun loadProducts() {
        db = FirebaseDatabase.getInstance().reference.child("products")

        productsValueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (!isAdded || _binding == null) return

                productList.clear()

                for (s in snapshot.children) {
                    val product = s.getValue(Product::class.java)
                    if (product != null) {
                        productList.add(product)
                    }
                }

                filter(binding.etSearch.text?.toString() ?: "")
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        db.addValueEventListener(productsValueEventListener)
    }

    private fun filter(text: String) {

        if (_binding == null) return
        if (!brandsLoaded || !categoriesLoaded) return

        val filteredList = ArrayList<Product>()
        val searchText = text.lowercase(Locale.getDefault())

        if (searchText.isEmpty()) {
            filteredList.addAll(productList)
        } else {
            for (item in productList) {

                val brandName = brandsMap[item.brandId]?.lowercase(Locale.getDefault()) ?: ""
                val categoryName = categoriesMap[item.categoryId]?.lowercase(Locale.getDefault()) ?: ""

                if (item.name.lowercase(Locale.getDefault()).contains(searchText) ||
                    brandName.contains(searchText) ||
                    categoryName.contains(searchText)
                ) {
                    filteredList.add(item)
                }
            }
        }

        if (this::adapter.isInitialized) {
            adapter.filterList(filteredList)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (this::db.isInitialized && this::productsValueEventListener.isInitialized) {
            db.removeEventListener(productsValueEventListener)
        }

        _binding = null
    }
}
