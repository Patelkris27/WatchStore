package com.example.watchstore

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AlertDialog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoToRegister = findViewById<Button>(R.id.btnGoToRegister)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        auth = FirebaseAuth.getInstance()
        val tvForgotPassword = findViewById<TextView>(R.id.tvForgotPassword)

        tvForgotPassword.setOnClickListener {
            showForgotPasswordDialog()
        }


        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    val user = auth.currentUser
                    if (user != null && user.isEmailVerified) {
                        startActivity(Intent(this, AdminHomeActivity::class.java))
                        finish()
                    } else {
                        auth.signOut()
                        Toast.makeText(this, "Verify your email before login", Toast.LENGTH_LONG).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "You are not registered!", Toast.LENGTH_SHORT).show()
                }
        }

        btnGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private fun showForgotPasswordDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_forgot_password, null)

        val etEmail = view.findViewById<EditText>(R.id.etEmail)
        val btnSend = view.findViewById<Button>(R.id.btnSend)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)
        val progress = view.findViewById<ProgressBar>(R.id.progressSend)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSend.isEnabled = false
            progress.visibility = View.VISIBLE

            FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    dialog.dismiss()
                    showSuccessDialog()
                }
                .addOnFailureListener {
                    btnSend.isEnabled = true
                    progress.visibility = View.GONE
                    Toast.makeText(this, "Failed to send email", Toast.LENGTH_SHORT).show()
                }
        }

        dialog.show()
    }
    private fun showSuccessDialog() {
        val view = layoutInflater.inflate(R.layout.dialog_success, null)
        val btnOk = view.findViewById<Button>(R.id.btnOk)

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .create()

        dialog.window?.attributes?.windowAnimations =
            android.R.style.Animation_Dialog

        btnOk.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


}
