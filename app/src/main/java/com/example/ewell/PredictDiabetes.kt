package com.example.ewell

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PredictDiabetes : AppCompatActivity() {

    // Declare UI elements
    private lateinit var backButton: ImageView
    private lateinit var glucoseEditText: EditText
    private lateinit var bpEditText: EditText
    private lateinit var predictButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict_diabetes)

        backButton = findViewById(R.id.backbuttondiabetes)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Updated for Android 13+ compatibility
        }

        // Initialize UI elements
        glucoseEditText = findViewById(R.id.glucose)
        bpEditText = findViewById(R.id.bp)
        predictButton = findViewById(R.id.predict)
        resultTextView = findViewById(R.id.display_diabetes_result)

        // Set click listener for the predict button
        predictButton.setOnClickListener {
            predictDiabetes()
        }
    }

    /**
     * Function to predict diabetes based on input values
     */
    private fun predictDiabetes() {
        val glucoseText = glucoseEditText.text.toString()
        val bpText = bpEditText.text.toString()

        // Validate input
        if (glucoseText.isEmpty() || bpText.isEmpty()) {
            Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val glucose = glucoseText.toInt()
            val bp = bpText.toInt()

            // Simple prediction logic (replace with your actual prediction algorithm)
            val predictionResult = performPrediction(glucose, bp)

            // Display result
            resultTextView.text = predictionResult

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Function to perform the diabetes prediction
     * This is a placeholder implementation - replace with your actual algorithm
     */
    private fun performPrediction(glucose: Int, bp: Int): String {
        // Simple threshold-based prediction (for demonstration purposes)
        return when {
            glucose > 125 && bp > 80 -> "High Risk"
            glucose > 100 || bp > 80 -> "Moderate Risk"
            else -> "Low Risk"
        }
    }

    // You can add more helper functions as needed for your prediction logic
}