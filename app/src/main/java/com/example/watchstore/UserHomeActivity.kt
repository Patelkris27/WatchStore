package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.*
import me.relex.circleindicator.CircleIndicator3
import java.util.*

class UserHomeActivity : AppCompatActivity() {

    private lateinit var rvProducts: RecyclerView
    private val list = ArrayList<Product>()
    private lateinit var db: DatabaseReference
    private var selectedBrand: String? = null
    private var selectedCategory: String? = null

    // Banner
    private lateinit var viewPager: ViewPager2
    private lateinit var indicator: CircleIndicator3
    private lateinit var bannerAdapter: BannerAdapter
    private val bannerImages = listOf(
        R.drawable.banner1, // Replace with your banner images
        R.drawable.banner2,
        R.drawable.banner3
    )
    private val handler = Handler(Looper.getMainLooper())
    private var currentPage = 0
    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home_user)

        // Banner setup
        viewPager = findViewById(R.id.viewPagerBanner)
        indicator = findViewById(R.id.indicator)

        bannerAdapter = BannerAdapter(bannerImages)
        viewPager.adapter = bannerAdapter
        indicator.setViewPager(viewPager)

        // Auto scroll
        createSlideShow()


        rvProducts = findViewById(R.id.rvUserProducts)
        rvProducts.layoutManager = GridLayoutManager(this, 2)
        val rvBrands = findViewById<RecyclerView>(R.id.rvBrands)
        val rvCategories = findViewById<RecyclerView>(R.id.rvCategories)

        rvBrands.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)
        rvCategories.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        loadFilters("brands", rvBrands) { brandId ->
            selectedBrand = brandId
            applyFilter()
        }

        loadFilters("categories", rvCategories) { categoryId ->
            selectedCategory = categoryId
            applyFilter()
        }


        db = FirebaseDatabase.getInstance().reference.child("products")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                list.clear()

                for (s in snapshot.children) {
                    list.add(
                        Product(
                            id = s.key ?: "",
                            name = s.child("name").value.toString(),
                            price = s.child("price").value.toString(),
                            imageUrl = s.child("imageUrl").value.toString(),
                            brandId = s.child("brandId").value.toString(),
                            categoryId = s.child("categoryId").value.toString(),
                            stock = s.child("stock").getValue(Int::class.java) ?: 0
                        )
                    )
                }

                rvProducts.adapter = UserProductAdapter(list)

            }

            override fun onCancelled(error: DatabaseError) {}
        })
        findViewById<ImageView>(R.id.btnCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
        findViewById<Button>(R.id.btnMyOrders).setOnClickListener {
            startActivity(Intent(this, UserOrdersActivity::class.java))
        }

    }

    private fun createSlideShow() {
        val runnable = Runnable {
            if (currentPage == bannerImages.size) {
                currentPage = 0
            }
            viewPager.setCurrentItem(currentPage++, true)
        }

        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                handler.post(runnable)
            }
        }, 3000, 3000)
    }

    private fun loadFilters(
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

    private fun applyFilter() {
        val filtered = list.filter {
            (selectedBrand == null || it.brandId == selectedBrand) &&
                    (selectedCategory == null || it.categoryId == selectedCategory)
        }
        rvProducts.adapter = UserProductAdapter(filtered)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
    }

}
