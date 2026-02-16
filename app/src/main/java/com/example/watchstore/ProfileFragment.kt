package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.watchstore.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var db: DatabaseReference
    private var userListener: ValueEventListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().reference.child("users")

        binding.btnMyOrders.setOnClickListener {
            startActivity(Intent(requireActivity(), UserOrdersActivity::class.java))
        }

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(requireActivity(), MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        loadUserProfile()
    }

    private fun loadUserProfile() {

        val user = auth.currentUser ?: return

        if (_binding == null) return

        binding.tvEmail.text = user.email

        userListener = object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {

                if (_binding == null) return   // 🔥 CRASH FIX

                val userProfile = snapshot.getValue(User::class.java)

                if (userProfile != null) {
                    binding.tvUsername.text = userProfile.name

                    // If you store profile image
                    // if (!userProfile.profileImageUrl.isNullOrEmpty()) {
                    //     Glide.with(requireContext())
                    //         .load(userProfile.profileImageUrl)
                    //         .into(binding.profileImage)
                    // }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        }

        db.child(user.uid).addListenerForSingleValueEvent(userListener!!)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val user = auth.currentUser
        if (userListener != null && user != null) {
            db.child(user.uid).removeEventListener(userListener!!)
        }

        _binding = null
    }
}
