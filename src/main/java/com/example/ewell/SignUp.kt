package com.example.ewell

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

import com.hbb20.CountryCodePicker

class SignUp : AppCompatActivity() {

    private lateinit var backButton: ImageView
    private lateinit var countryCodePicker: CountryCodePicker
    private lateinit var mobileNo: EditText
    private lateinit var phoneNumberUtil: PhoneNumberUtil
    private lateinit var mobileInputLayout: com.google.android.material.textfield.TextInputLayout

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Initialize phone number utility
        phoneNumberUtil = PhoneNumberUtil.getInstance()

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
        mobileNo = findViewById<EditText>(R.id.MobileNo)
        mobileInputLayout = findViewById(R.id.mobileInputLayout)
        countryCodePicker = findViewById(R.id.countryCodePicker)
        val registerButton = findViewById<Button>(R.id.Register)
        val backToLogin = findViewById<TextView>(R.id.backtologin)

        // Setup enhanced phone validation
        setupEnhancedPhoneValidation()

        registerButton.setOnClickListener {
            val firstNameText = firstName.text.toString().trim()
            val lastNameText = lastName.text.toString().trim()
            val emailText = email.text.toString().trim()
            val passwordText = password.text.toString().trim()
            val mobileNoText = mobileNo.text.toString().trim()

            // Basic field validation
            if (firstNameText.isEmpty() || lastNameText.isEmpty() || emailText.isEmpty() ||
                passwordText.isEmpty() || mobileNoText.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Email validation
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
                Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Enhanced password validation
            if (passwordText.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for capital letter
            if (!passwordText.any { it.isUpperCase() }) {
                Toast.makeText(this, "Password must contain at least one capital letter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for special character
            if (!passwordText.any { !it.isLetterOrDigit() }) {
                Toast.makeText(this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for number
            if (!passwordText.any { it.isDigit() }) {
                Toast.makeText(this, "Password must contain at least one number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Phone number validation using libphonenumber
            if (mobileNoText.isEmpty()) {
                Toast.makeText(this, "Please enter a mobile number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isPhoneNumberValid(mobileNoText, countryCodePicker.selectedCountryNameCode)) {
                Toast.makeText(
                    this,
                    "Please enter a valid phone number for ${countryCodePicker.selectedCountryName}",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Get formatted phone number for storage
            val formattedPhoneNumber = formatPhoneNumber(mobileNoText, countryCodePicker.selectedCountryNameCode)

            // Create user account
            auth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        // Set user as inactive until verification
                        val user = User(firstNameText, lastNameText, emailText, formattedPhoneNumber, false)

                        // Send verification email
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnCompleteListener { verificationTask ->
                                if (verificationTask.isSuccessful) {
                                    // Save user data to database
                                    userId?.let {
                                        databaseReference.child(it).setValue(user)
                                            .addOnCompleteListener {
                                                Toast.makeText(
                                                    this,
                                                    "Sign up successful! Please check your email to verify your account.",
                                                    Toast.LENGTH_LONG
                                                ).show()

                                                // Navigate to verification pending screen
                                                val intent = Intent(this, VerificationPendingActivity::class.java)
                                                intent.putExtra("email", emailText)
                                                startActivity(intent)
                                                finish()
                                            }
                                    }
                                } else {
                                    Toast.makeText(
                                        this,
                                        "Failed to send verification email: ${verificationTask.exception?.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    // Delete the created user if email verification fails
                                    auth.currentUser?.delete()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        backToLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun setupEnhancedPhoneValidation() {
        // Register the EditText with the picker for basic validation
        countryCodePicker.registerCarrierNumberEditText(mobileNo)

        // Real-time validation feedback
        mobileNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.isNotEmpty()) {
                    if (!isPhoneNumberValid(s.toString(), countryCodePicker.selectedCountryNameCode)) {
                        mobileNo.error = "Invalid format for ${countryCodePicker.selectedCountryName}"
                    } else {
                        mobileNo.error = null
                    }
                }
            }
        })

        // Update when country changes
        countryCodePicker.setOnCountryChangeListener {
            mobileNo.setText("")
            mobileNo.error = null
            updatePhoneHint()
        }

        // Initial hint
        updatePhoneHint()
    }

    private fun updatePhoneHint() {
        try {
            val exampleNumber = phoneNumberUtil.getExampleNumber(countryCodePicker.selectedCountryNameCode)
            if (exampleNumber != null) {
                val formattedExample = phoneNumberUtil.format(
                    exampleNumber,
                    PhoneNumberUtil.PhoneNumberFormat.NATIONAL
                )
                // Update the hint in TextInputLayout instead of EditText
                mobileInputLayout.hint = "Mobile Number (e.g., $formattedExample)"
            } else {
                mobileInputLayout.hint = "Mobile Number"
            }
        } catch (e: Exception) {
            mobileInputLayout.hint = "Mobile Number"
        }
    }

    private fun isPhoneNumberValid(phoneNumber: String, countryCode: String): Boolean {
        return try {
            val numberProto = phoneNumberUtil.parse(phoneNumber, countryCode)

            // Additional check to verify the number is mobile (optional)
            // Note: This check isn't 100% reliable in all countries
            val numberType = phoneNumberUtil.getNumberType(numberProto)
            val isMobile = numberType == PhoneNumberUtil.PhoneNumberType.MOBILE ||
                    numberType == PhoneNumberUtil.PhoneNumberType.FIXED_LINE_OR_MOBILE

            phoneNumberUtil.isValidNumber(numberProto) && isMobile
        } catch (e: NumberParseException) {
            false
        }
    }

    private fun formatPhoneNumber(phoneNumber: String, countryCode: String): String {
        return try {
            val numberProto = phoneNumberUtil.parse(phoneNumber, countryCode)
            phoneNumberUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164)
        } catch (e: NumberParseException) {
            countryCodePicker.selectedCountryCodeWithPlus + phoneNumber // Fallback format
        }
    }
}

// Updated User data class with formatted phone number field
data class User(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val fullPhoneNumber: String = "",  // Stores E.164 formatted phone number
    val isEmailVerified: Boolean = false
)