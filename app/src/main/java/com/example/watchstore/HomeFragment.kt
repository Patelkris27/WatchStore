package com.example.watchstore

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchstore.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val productList = ArrayList<Product>()
    private lateinit var db: DatabaseReference
    private var selectedBrand: String? = null
    private var selectedCategory: String? = null
    private lateinit var productsValueEventListener: ValueEventListener

    // Banner
    private val bannerImages = listOf(
        R.drawable.banner1, // Replace with your banner images
        R.drawable.banner2,
        R.drawable.banner3
    )
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private var timer: Timer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBanner()
        setupRecyclerViews()
        loadFilters()
        loadProducts()

        binding.ivSearch.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, WatchlistFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupBanner() {
        val bannerAdapter = BannerAdapter(bannerImages)
        binding.viewPagerBanner.adapter = bannerAdapter

        createSlideShow()
    }

    private fun createSlideShow() {
        val runnable = Runnable {
            if (currentPage == bannerImages.size) {
                currentPage = 0
            }
            binding.viewPagerBanner.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }, 3000, 3000)
    }

    private fun setupRecyclerViews() {
        binding.rvUserProducts.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvBrands.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
    }

    private fun loadFilters() {
        loadFilterData("brands", binding.rvBrands) { brandId ->
            selectedBrand = brandId
            applyFilter()
        }

        loadFilterData("categories", binding.rvCategories) { categoryId ->
            selectedCategory = categoryId
            applyFilter()
        }
    }

    private fun loadFilterData(
        node: String,
        rv: RecyclerView,
        onSelect: (String?) -> Unit
    ) {
        FirebaseDatabase.getInstance().reference.child(node)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = ArrayList<Pair<String, String>>()
                    items.add("All" to "")

                    for (s in snapshot.children) {
                        val name = when {
                            s.value is String -> s.value.toString()
                            s.child("name").exists() -> s.child("name").value.toString()
                            else -> continue
                        }
                        items.add(name to s.key!!)
                    }

                    rv.adapter = FilterAdapter(items) { id ->
                        onSelect(if (id.isNullOrEmpty()) null else id)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadProducts() {
        db = FirebaseDatabase.getInstance().reference.child("products")
        productsValueEventListener = object : ValueEventListener {
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
                        stock = s.child("stock").getValue(Long::class.java) ?: 0L
                    )
                    productList.add(product)
                }
                applyFilter()
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        db.addValueEventListener(productsValueEventListener)
    }

    private fun applyFilter() {
        val filtered = productList.filter {
            (selectedBrand == null || it.brandId == selectedBrand) &&
                    (selectedCategory == null || it.categoryId == selectedCategory)
        }
        binding.rvUserProducts.adapter = UserProductAdapter(filtered)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timer?.cancel()
        db.removeEventListener(productsValueEventListener)
        _binding = null
    }
}
