package com.example.ewell

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class HealthDetails : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_details)

        backButton = findViewById(R.id.backbuttonhealthdetails)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Updated for Android 13+ compatibility
        }

        val genderSpinner: Spinner = findViewById(R.id.gender)
        val date: TextView = findViewById(R.id.date)
        val pickDateBtn: LinearLayout = findViewById(R.id.pickDateBtn)
        val weightInput: EditText = findViewById(R.id.Weight)
        val heightInput: EditText = findViewById(R.id.Height)
        val stepsInput: EditText = findViewById(R.id.Steps)
        val nextButton: Button = findViewById(R.id.NextHomeBtn)

        val genders = arrayOf("Male", "Female", "Other")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, genders)
        genderSpinner.adapter = adapter

        // Create a function to show date picker to avoid code duplication
        fun showDatePicker() {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                date.text = formattedDate
            }, year, month, day)
            datePickerDialog.show()
        }

        // Set click listeners for both the date text and the pick date button
        pickDateBtn.setOnClickListener {
            showDatePicker()
        }

        // Add click listener to the dateText itself
        date.setOnClickListener {
            showDatePicker()
        }

        nextButton.setOnClickListener {
            val selectedGender = genderSpinner.selectedItem.toString()
            val dob = date.text.toString()
            val weight = weightInput.text.toString()
            val height = heightInput.text.toString()
            val steps = stepsInput.text.toString()

            if (dob.isEmpty() || weight.isEmpty() || height.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setOnClickListener
                val userHealthData = hashMapOf(
                    "gender" to selectedGender,
                    "dob" to dob,
                    "weight" to weight,
                    "height" to height,
                    "steps" to steps
                )

                db.collection("users").document(userId).set(userHealthData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Data Saved Successfully", Toast.LENGTH_SHORT).show()
                        nextButton.postDelayed({
                            startActivity(Intent(this, Profile::class.java))
                            finish() // Add this to close the current activity
                        }, 1000) // 1 second delay
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}