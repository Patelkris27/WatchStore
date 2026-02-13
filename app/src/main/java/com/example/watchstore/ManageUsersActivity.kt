package com.example.watchstore

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.watchstore.databinding.ActivityManageUsersBinding
import com.google.firebase.database.*

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageUsersBinding
    private lateinit var db: DatabaseReference
    private val userList = ArrayList<User>()
    private lateinit var valueEventListener: ValueEventListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().reference.child("users")

        setupRecyclerView()
        loadUsers()
    }

    private fun setupRecyclerView() {
        binding.rvUsers.layoutManager = LinearLayoutManager(this)
    }

    private fun loadUsers() {
        valueEventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (s in snapshot.children) {
                    val name = s.child("name").value.toString()
                    val email = s.child("email").value.toString()
                    s.key?.let {
                        userList.add(User(it, name, email))
                    }
                }
                binding.rvUsers.adapter = UserAdapter(userList)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        db.addValueEventListener(valueEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        db.removeEventListener(valueEventListener)
    }
}
