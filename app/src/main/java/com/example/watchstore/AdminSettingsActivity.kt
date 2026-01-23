package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class AdminSettingsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_settings)

        auth = FirebaseAuth.getInstance()

        val etCurrent = findViewById<EditText>(R.id.etCurrentPassword)
        val etNew = findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassword)
        val btnChange = findViewById<Button>(R.id.btnChangePassword)

        val btnProfile = findViewById<Button>(R.id.btnViewProfile)
        val btnRefresh = findViewById<Button>(R.id.btnRefreshData)
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        val tvVersion = findViewById<TextView>(R.id.tvVersion)

        tvVersion.text = "Version 1.0"

        btnChange.setOnClickListener {
            val currentPass = etCurrent.text.toString()
            val newPass = etNew.text.toString()
            val confirmPass = etConfirm.text.toString()

            val user = auth.currentUser ?: return@setOnClickListener
            val email = user.email ?: return@setOnClickListener

            if (newPass.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPass != confirmPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val credential = EmailAuthProvider.getCredential(email, currentPass)

            user.reauthenticate(credential)
                .addOnSuccessListener {
                    user.updatePassword(newPass)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                            etCurrent.text.clear()
                            etNew.text.clear()
                            etConfirm.text.clear()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Password update failed", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show()
                }
        }

        btnProfile.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Admin Profile")
                .setMessage(
                    "Email: ${auth.currentUser?.email}\nRole: Admin"
                )
                .setPositiveButton("OK", null)
                .show()
        }

        btnRefresh.setOnClickListener {
            Toast.makeText(this, "App data refreshed", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes") { _, _ ->
                    auth.signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
