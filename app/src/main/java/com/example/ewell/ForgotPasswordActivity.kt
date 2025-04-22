package com.example.ewell

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var resetEmail: EditText
    private lateinit var resetPasswordBtn: Button
    private lateinit var backToLoginBtn: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Bind Views
        backButton = findViewById(R.id.backButton)
        resetEmail = findViewById(R.id.resetEmail)
        resetPasswordBtn = findViewById(R.id.resetPasswordBtn)
        backToLoginBtn = findViewById(R.id.backToLoginBtn)

        // Set Click Listeners
        backButton.setOnClickListener {
            onBackPressed()
        }

        backToLoginBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        resetPasswordBtn.setOnClickListener {
            val email = resetEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email address.", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password reset email sent. Please check your inbox.", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Error: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }
}
