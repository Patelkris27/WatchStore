package com.example.watchstore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.watchstore.databinding.FragmentWatchlistBinding
import com.google.firebase.database.*

class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    private val productList = ArrayList<Product>()
    private lateinit var db: DatabaseReference

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
        loadProducts()
    }

    private fun loadProducts() {
        db = FirebaseDatabase.getInstance().reference.child("products")
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (s in snapshot.children) {
                    val product = Product(
                        id = s.key ?: "",
                        name = s.child("name").value.toString(),
                        price = s.child("price").getValue(Double::class.java) ?: 0.0,
                        imageUrl = s.child("imageUrl").value.toString(),
                        brandId = s.child("brandId").value.toString(),
                        categoryId = s.child("categoryId").value.toString(),
                        stock = s.child("stock").getValue(Int::class.java) ?: 0
                    )
                    productList.add(product)
                }
                binding.rvWatchlist.adapter = UserProductAdapter(productList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}