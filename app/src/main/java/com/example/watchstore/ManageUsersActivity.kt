package com.example.watchstore

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ManageUsersActivity : AppCompatActivity() {

    private lateinit var rvUsers: RecyclerView
    private lateinit var db: DatabaseReference
    private val list = ArrayList<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_manage_users)

        rvUsers = findViewById(R.id.rvUsers)
        rvUsers.layoutManager = LinearLayoutManager(this)

        db = FirebaseDatabase.getInstance().reference.child("users")

        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list.clear()
                for (s in snapshot.children) {
                    val name = s.child("name").value.toString()
                    val email = s.child("email").value.toString()
                    list.add(User(s.key!!, name, email))
                }
                rvUsers.adapter = UserAdapter(list)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
