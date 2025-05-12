package com.example.ewell

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class StressMngt : AppCompatActivity() {

    private lateinit var backButton: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stress_mngt)

        val btnPredict: Button = findViewById(R.id.btnPredict)
        val displayStressLevel: TextView = findViewById(R.id.display_stress_level)

        backButton = findViewById(R.id.backbuttonstressmngt)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed() // Updated for Android 13+ compatibility
        }


        val radioGroups = listOf(
            findViewById<RadioGroup>(R.id.radioGroup1),
            findViewById<RadioGroup>(R.id.radioGroup2),
            findViewById<RadioGroup>(R.id.radioGroup3),
            findViewById<RadioGroup>(R.id.radioGroup4),
            findViewById<RadioGroup>(R.id.radioGroup5)
        )

        btnPredict.setOnClickListener {
            var stressScore = 0

            for (radioGroup in radioGroups) {
                val selectedId = radioGroup.checkedRadioButtonId
                if (selectedId != -1) {
                    val selectedRadioButton = findViewById<RadioButton>(selectedId)
                    if (selectedRadioButton.text.toString().trim() == "Yes") {
                        stressScore++
                    }
                }
            }


            val stressLevel = when (stressScore) {
                0, 1 -> "Low Stress"
                2, 3 -> "Moderate Stress"
                4, 5 -> "High Stress"
                else -> "Unknown"
            }

            displayStressLevel.text = "Stress Level: $stressLevel"
        }
    }
}