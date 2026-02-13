package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AlertDialog
import com.example.watchstore.databinding.ActivityMainBinding
import com.example.watchstore.databinding.DialogForgotPasswordBinding
import com.example.watchstore.databinding.DialogSuccessBinding
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_email_and_password), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val user = auth.currentUser ?: return@addOnSuccessListener
                    if (!user.isEmailVerified) {
                        auth.signOut()
                        Toast.makeText(
                            this,
                            getString(R.string.verify_email_before_login),
                            Toast.LENGTH_LONG
                        ).show()
                        return@addOnSuccessListener
                    }
                    val uid = user.uid
                    FirebaseDatabase.getInstance().reference
                        .child("users")
                        .child(uid)
                        .child("role")
                        .get()
                        .addOnSuccessListener { snapshot ->
                            when (snapshot.value.toString()) {
                                "admin" -> {
                                    startActivity(Intent(this, AdminHomeActivity::class.java))
                                    finish()
                                }
                                "user" -> {
                                    startActivity(Intent(this, UserHomeActivity::class.java))
                                    finish()
                                }
                                else -> {
                                    auth.signOut()
                                    Toast.makeText(this, getString(R.string.role_not_assigned), Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        getString(R.string.invalid_email_or_password),
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

        binding.btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showForgotPasswordDialog() {
        val dialogBinding = DialogForgotPasswordBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnSend.setOnClickListener {
            val email = dialogBinding.etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, getString(R.string.enter_email), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            dialogBinding.btnSend.isEnabled = false
            dialogBinding.progressSend.visibility = View.VISIBLE

            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    dialog.dismiss()
                    showSuccessDialog()
                }
                .addOnFailureListener {
                    dialogBinding.btnSend.isEnabled = true
                    dialogBinding.progressSend.visibility = View.GONE
                    Toast.makeText(this, getString(R.string.failed_to_send_email), Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }

    private fun showSuccessDialog() {
        val dialogBinding = DialogSuccessBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogBinding.root)
            .create()

        dialog.window?.attributes?.windowAnimations =
            android.R.style.Animation_Dialog

        dialogBinding.btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.exit_app))
            .setMessage(getString(R.string.exit_app_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                finishAffinity()
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }
}
