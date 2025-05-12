package com.example.ewell

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class VerificationPendingActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_pending)

        auth = FirebaseAuth.getInstance()
        userEmail = intent.getStringExtra("email") ?: ""

        val emailTextView = findViewById<TextView>(R.id.tvEmail)
        emailTextView.text = userEmail

        val resendButton = findViewById<Button>(R.id.btnResendEmail)
        val verifyButton = findViewById<Button>(R.id.btnVerifyNow)
        val loginButton = findViewById<Button>(R.id.btnBackToLogin)

        // Resend verification email
        resendButton.setOnClickListener {
            auth.currentUser?.sendEmailVerification()
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this,
                            "Verification email sent again. Please check your inbox.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Failed to resend email: ${task.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }

        // Check if email is verified
        verifyButton.setOnClickListener {
            auth.currentUser?.reload()?.addOnCompleteListener {
                if (auth.currentUser?.isEmailVerified == true) {
                    // Update user verification status in database
                    updateUserVerificationStatus()
                    Toast.makeText(
                        this,
                        "Email verified successfully! You can now login.",
                        Toast.LENGTH_LONG
                    ).show()
                    // Redirect to login screen
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Your email is not verified yet. Please check your inbox.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        // Go back to login
        loginButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updateUserVerificationStatus() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val databaseReference = FirebaseDatabase.getInstance().reference
                .child("Users").child(userId)

            databaseReference.child("emailVerified").setValue(true)
        }
    }
}