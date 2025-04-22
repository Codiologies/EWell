package com.example.ewell

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var backButton: ImageView

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        backButton = findViewById(R.id.backbuttonsignup)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Updated for Android 13+ compatibility
        }

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val firstName = findViewById<EditText>(R.id.FirstName)
        val lastName = findViewById<EditText>(R.id.LastName)
        val email = findViewById<EditText>(R.id.Email)
        val password = findViewById<EditText>(R.id.Password)
        val mobileNo = findViewById<EditText>(R.id.MobileNo)
        val registerButton = findViewById<Button>(R.id.Register)
        val backToLogin = findViewById<TextView>(R.id.backtologin)

        registerButton.setOnClickListener {
            val firstNameText = firstName.text.toString().trim()
            val lastNameText = lastName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val mobileNoText = mobileNo.text.toString().trim()

            if (firstNameText.isEmpty() || lastNameText.isEmpty() || emailText.isEmpty() ||
                passwordText.isEmpty() || mobileNoText.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (passwordText.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!mobileNoText.matches("\\d{10}".toRegex())) {
                Toast.makeText(this, "Enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            else {
                auth.createUserWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            val user = User(firstNameText, lastNameText, emailText, mobileNoText)
                            userId?.let {
                                databaseReference.child(it).setValue(user)
                                    .addOnCompleteListener {
                                        Toast.makeText(this, "Sign Up Successful", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this, MainActivity::class.java))
                                        finish()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        backToLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}

// User.kt
data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val mobileNo: String = ""
)
