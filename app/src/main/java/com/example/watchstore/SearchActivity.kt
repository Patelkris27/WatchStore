package com.example.watchstore

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class SearchActivity : AppCompatActivity() {

    private lateinit var etSearch: EditText
    private lateinit var rvSearchResults: RecyclerView
    private val productList = ArrayList<Product>()
    private lateinit var adapter: UserProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        etSearch = findViewById(R.id.etSearch)
        rvSearchResults = findViewById(R.id.rvSearchResults)

        adapter = UserProductAdapter(this, productList)
        rvSearchResults.layoutManager = GridLayoutManager(this, 2)
        rvSearchResults.adapter = adapter

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchForWatches(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchForWatches(query: String) {
        val productsRef = FirebaseDatabase.getInstance().reference.child("products")
        val searchQuery = productsRef.orderByChild("name")
            .startAt(query)
            .endAt(query + "\uf8ff")

        searchQuery.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (s in snapshot.children) {
                    val product = s.getValue(Product::class.java)
                    if (product != null) {
                        productList.add(product)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}
