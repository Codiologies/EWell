package com.example.ewell


import android.content.Intent

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class Profile : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var pfLName: TextView
    private lateinit var pfirstName: TextView
    private lateinit var plastName: TextView
    private lateinit var puserName: TextView
    private lateinit var stepsGoal: TextView
    private lateinit var genderText: TextView
    private lateinit var dobText: TextView
    private lateinit var weightText: TextView
    private lateinit var heightText: TextView
    private lateinit var logoutThisDevice: LinearLayout
    private lateinit var logoutAllDevices: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var downloadMedicalRecords: LinearLayout
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        backButton = findViewById(R.id.backbuttonprofile)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Updated for Android 13+ compatibility
        }


        initializeViews()
        databaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        auth = FirebaseAuth.getInstance()
        setUserData()
        setupBottomNavigation()
        setupClickListeners()
    }

    private fun initializeViews() {
        pfLName = findViewById(R.id.PFLName)
        pfirstName = findViewById(R.id.PFirstName)
        plastName = findViewById(R.id.PLastName)
        puserName = findViewById(R.id.PUserName)
        stepsGoal = findViewById(R.id.steps)
        genderText = findViewById(R.id.gender)
        dobText = findViewById(R.id.dob)
        weightText = findViewById(R.id.Weight)
        heightText = findViewById(R.id.Height)
        logoutThisDevice = findViewById(R.id.logout_this_device)
        logoutAllDevices = findViewById(R.id.logout_all_devices)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        downloadMedicalRecords = findViewById(R.id.download_medical_records)
    }

    private fun setUserData() {
        val userId = auth.currentUser?.uid ?: return

        // Fetch data from Realtime Database
        databaseReference.child(userId).get().addOnSuccessListener { dataSnapshot ->
            if (dataSnapshot.exists()) {
                val firstName = dataSnapshot.child("firstName").value?.toString() ?: "Unknown"
                val lastName = dataSnapshot.child("lastName").value?.toString() ?: "User"
                pfirstName.text = firstName
                plastName.text = lastName
                puserName.text = auth.currentUser?.email
                val initials = "${firstName.firstOrNull()?.uppercaseChar() ?: '?'}${lastName.firstOrNull()?.uppercaseChar() ?: '?'}"
                pfLName.text = initials
            } else {
                Toast.makeText(this, "User data not found in Realtime Database.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error fetching data: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        // Fetch data from Firestore
        db.collection("users").document(userId).get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val gender = document.getString("gender") ?: "-"
                val dob = document.getString("dob") ?: "-"
                val weight = document.getString("weight") ?: "-"
                val height = document.getString("height") ?: "-"
                val steps = document.getString("steps") ?: "-"

                genderText.text = gender
                dobText.text = dob
                weightText.text = weight
                heightText.text = height
                stepsGoal.text = steps
            } else {
                Toast.makeText(this, "Health data not found in Firestore.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error fetching data from Firestore: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> {
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.chatbot -> {
                    startActivity(Intent(this, Chatbot::class.java))
                    true
                }
                R.id.profile -> true
                else -> false
            }
        }

        bottomNavigation.selectedItemId = R.id.profile
    }

    private fun setupClickListeners() {
        logoutThisDevice.setOnClickListener {
            showLogoutConfirmationDialog(false)
        }

        logoutAllDevices.setOnClickListener {
            showLogoutConfirmationDialog(true)
        }

        downloadMedicalRecords.setOnClickListener {
            Toast.makeText(this, "Medical record downloaded", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLogoutConfirmationDialog(logoutAllDevices: Boolean) {
        val title = if (logoutAllDevices) "Logout from all devices?" else "Logout from this device?"
        val message = if (logoutAllDevices) "This will log you out from all devices. Are you sure?" else "You will be logged out from this device. Are you sure?"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Yes") { _, _ -> performLogout(logoutAllDevices) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun performLogout(logoutAllDevices: Boolean) {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, if (logoutAllDevices) "Logged out from all devices" else "Logged out from this device", Toast.LENGTH_SHORT).show()

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
