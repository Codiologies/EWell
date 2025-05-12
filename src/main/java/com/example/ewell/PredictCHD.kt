package com.example.ewell

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PredictCHD : AppCompatActivity() {

    // Declare UI elements
    private lateinit var backButton: ImageView
    private lateinit var cholesterolEditText: EditText
    private lateinit var bpEditText: EditText
    private lateinit var glucoseEditText: EditText
    private lateinit var predictButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict_c_h_d)

        backButton = findViewById(R.id.backbuttonchd)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Updated for Android 13+ compatibility
        }

        // Initialize UI elements
        cholesterolEditText = findViewById(R.id.cholesterol)
        bpEditText = findViewById(R.id.bp)
        glucoseEditText = findViewById(R.id.glucose)
        predictButton = findViewById(R.id.predict)
        resultTextView = findViewById(R.id.display_chd_result)

        // Set click listener for the predict button
        predictButton.setOnClickListener {
            predictCHD()
        }
    }

    /**
     * Function to predict coronary heart disease based on input values
     */
    private fun predictCHD() {
        // Get input values
        val cholesterolText = cholesterolEditText.text.toString()
        val bpText = bpEditText.text.toString()
        val glucoseText = glucoseEditText.text.toString()

        // Validate input
        if (cholesterolText.isEmpty() || bpText.isEmpty() || glucoseText.isEmpty()) {
            Toast.makeText(this, "Please enter all values", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val cholesterol = cholesterolText.toInt()
            val bp = bpText.toInt()
            val glucose = glucoseText.toInt()

            // Perform prediction and display result
            val predictionResult = performCHDPrediction(cholesterol, bp, glucose)
            resultTextView.text = predictionResult

        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Please enter valid numbers", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Function to perform the CHD prediction
     * This is a placeholder implementation - replace with your actual algorithm
     */
    private fun performCHDPrediction(cholesterol: Int, bp: Int, glucose: Int): String {
        // Simple threshold-based prediction (for demonstration purposes)
        // In a real application, you would use a validated medical algorithm or machine learning model

        var riskScore = 0

        // Calculate risk based on cholesterol
        when {
            cholesterol > 240 -> riskScore += 3
            cholesterol > 200 -> riskScore += 1
        }

        // Calculate risk based on blood pressure
        when {
            bp > 140 -> riskScore += 3
            bp > 120 -> riskScore += 1
        }

        // Calculate risk based on glucose
        when {
            glucose > 126 -> riskScore += 2
            glucose > 100 -> riskScore += 1
        }

        // Determine risk level based on total score
        return when {
            riskScore >= 5 -> "High Risk"
            riskScore >= 3 -> "Moderate Risk"
            else -> "Low Risk"
        }
    }

    // You can add more helper functions as needed for your prediction logic
}