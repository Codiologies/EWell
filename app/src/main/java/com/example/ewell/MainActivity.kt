package com.example.ewell

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    private val animationDuration = 1000L // 1 second

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference.child("Notifications")

        val email = findViewById<TextInputEditText>(R.id.Email)
        val password = findViewById<TextInputEditText>(R.id.Password)
        val loginButton = findViewById<Button>(R.id.LoginBtn)
        val signupTextView = findViewById<TextView>(R.id.SignupBtn)
        val forgotPasswordTextView = findViewById<TextView>(R.id.ForgotPasswordBtn)

        loginButton.setOnClickListener {
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()

            if (emailText.isEmpty() || passwordText.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            } else {
                showLoadingAnimation()

                auth.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            saveLoginNotificationToFirebase(emailText)
                            Handler(Looper.getMainLooper()).postDelayed({
                                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, HomePage::class.java))
                                finish()
                            }, animationDuration)
                        } else {
                            setContentView(R.layout.activity_main)
                            Toast.makeText(
                                this,
                                "Login Failed: ${task.exception?.localizedMessage}",
                                Toast.LENGTH_SHORT
                            ).show()

                            val emailAgain = findViewById<TextInputEditText>(R.id.Email)
                            val passwordAgain = findViewById<TextInputEditText>(R.id.Password)
                            val loginButtonAgain = findViewById<Button>(R.id.LoginBtn)
                            val signupTextViewAgain = findViewById<TextView>(R.id.SignupBtn)
                            val forgotPasswordTextViewAgain =
                                findViewById<TextView>(R.id.ForgotPasswordBtn)

                            emailAgain.setText(emailText)
                            setupClickListeners(
                                loginButtonAgain,
                                signupTextViewAgain,
                                forgotPasswordTextViewAgain
                            )
                        }
                    }
            }
        }

        setupClickListeners(loginButton, signupTextView, forgotPasswordTextView)
    }

    private fun setupClickListeners(
        loginButton: Button,
        signupTextView: TextView,
        forgotPasswordTextView: TextView
    ) {
        signupTextView.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }

        forgotPasswordTextView.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }
    }

    private fun showLoadingAnimation() {
        setContentView(R.layout.loading_animation)
        val logoImageView = findViewById<ImageView>(R.id.animatedLogo)
        val pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.logo_pulse)
        logoImageView.startAnimation(pulseAnimation)
    }

    // ✅ Save login notification to Firebase
    private fun saveLoginNotificationToFirebase(email: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userNotificationRef = dbRef.child(userId)
            val notificationId = userNotificationRef.push().key

            val notification = NotificationModel(
                notificationId = notificationId,
                message = "$email signed in successfully!",
                timestamp = System.currentTimeMillis().toString()
            )

            notificationId?.let {
                userNotificationRef.child(it).setValue(notification)
                    .addOnSuccessListener {
//                        Toast.makeText(this, "Notification saved successfully!", Toast.LENGTH_SHORT)
//                            .show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(
                            this,
                            "Failed to save notification: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        } else {
            Toast.makeText(this, "User not authenticated!", Toast.LENGTH_SHORT).show()
        }
    }
}

