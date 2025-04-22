package com.example.ewell

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PredictAsthma : AppCompatActivity() {

    // Declare UI components
    private lateinit var backButton: ImageView
    private lateinit var oxygenEditText: EditText
    private lateinit var heartRateEditText: EditText
    private lateinit var respirationEditText: EditText
    private lateinit var predictButton: Button
    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_predict_asthma)

        backButton = findViewById(R.id.backbuttonasthma)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Updated for Android 13+ compatibility
        }

        // Initialize UI components
        oxygenEditText = findViewById(R.id.oxygen)
        heartRateEditText = findViewById(R.id.heartrate)
        respirationEditText = findViewById(R.id.respiration)
        predictButton = findViewById(R.id.predict)
        resultTextView = findViewById(R.id.display_asthma_result)

        // Set up click listener for the predict button
        predictButton.setOnClickListener {
            predictAsthma()
        }
    }

    private fun predictAsthma() {
        // Validate input fields
        if (isInputValid()) {
            // Get input values
            val oxygen = oxygenEditText.text.toString().toFloat()
            val heartRate = heartRateEditText.text.toString().toFloat()
            val respiration = respirationEditText.text.toString().toFloat()

            // Process the prediction
            val result = calculateAsthmaRisk(oxygen, heartRate, respiration)

            // Display the result
            resultTextView.text = result
        } else {
            Toast.makeText(this, "Please fill all fields with valid values", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isInputValid(): Boolean {
        // Check if all fields are filled
        if (oxygenEditText.text.isNullOrEmpty() || heartRateEditText.text.isNullOrEmpty() ||
            respirationEditText.text.isNullOrEmpty()) {
            return false
        }

        try {
            // Check if input values are within valid ranges
            val oxygen = oxygenEditText.text.toString().toFloat()
            val heartRate = heartRateEditText.text.toString().toFloat()
            val respiration = respirationEditText.text.toString().toFloat()

            if (oxygen < 0 || oxygen > 100) return false
            if (heartRate < 30 || heartRate > 220) return false
            if (respiration < 0 || respiration > 60) return false

            return true
        } catch (e: NumberFormatException) {
            return false
        }
    }

    private fun calculateAsthmaRisk(oxygen: Float, heartRate: Float, respiration: Float): String {
        // This is a simplified example of an asthma risk calculation
        // In a real application, you would implement a more sophisticated algorithm
        // based on medical research and guidelines

        var risk = "Unknown"

        // Low oxygen levels can indicate an asthma problem
        if (oxygen < 90) {
            if (respiration > 30 && heartRate > 100) {
                risk = "High Risk"
            } else if (respiration > 20 || heartRate > 90) {
                risk = "Moderate Risk"
            } else {
                risk = "Low Risk"
            }
        } else if (oxygen < 95) {
            if (respiration > 25 && heartRate > 100) {
                risk = "Moderate Risk"
            } else {
                risk = "Low Risk"
            }
        } else {
            if (respiration > 30 || heartRate > 120) {
                risk = "Low Risk"
            } else {
                risk = "No Risk"
            }
        }

        return risk
    }
}