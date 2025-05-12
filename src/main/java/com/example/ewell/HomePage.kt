package com.example.ewell

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.*
import kotlin.concurrent.fixedRateTimer
import kotlin.random.Random

class HomePage : AppCompatActivity() {

    // UI components
    private lateinit var progressBar: android.widget.ProgressBar
    private lateinit var textViewProgress: TextView
    private lateinit var addBtn: FloatingActionButton
    private lateinit var glucoseBtn: FloatingActionButton
    private lateinit var bloodPressureBtn: FloatingActionButton
    private lateinit var medicalRecordsBtn: FloatingActionButton
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var drawerLayout: DrawerLayout

    // FAB menu state
    private var isFabMenuOpen = false

    // Timer for real-time updates
    private var updateTimer: Timer? = null

    // Current health metrics (replacing database data)
    private var currentSteps = 6000
    private var currentOxygen = 97
    private var currentCalories = 1500
    private var currentHeartRate = 75

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        // Initialize UI components
        initViews()

        // Set up toolbar and drawer
        setupToolbarAndDrawer()

        // Set up listeners
        setupFabMenu()
        setupBottomNavigation()
        setupHealthMetricsButtons()
        setupSwipeRefresh()

        // Start real-time updates
        startRealTimeUpdates()

        // Set up notification icon click event
        findViewById<ImageView>(R.id.notificationIcon).setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
    }

    private fun initViews() {
        // Progress components
        progressBar = findViewById(R.id.progress_bar)
        textViewProgress = findViewById(R.id.text_view_progress)

        // FAB buttons
        addBtn = findViewById(R.id.add_btn)
        glucoseBtn = findViewById(R.id.glucose_btn)
        bloodPressureBtn = findViewById(R.id.blood_pressure)
        medicalRecordsBtn = findViewById(R.id.add_medical_records)

        // Bottom navigation
        bottomNavigation = findViewById(R.id.bottom_navigation)

        // SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)

        // Set initial progress data
        updateProgress()
    }

    private fun setupToolbarAndDrawer() {
        // Toolbar setup
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)

        // Hamburger icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_menu_24)

        // Navigation Item Click
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Already on home page, no need to start new activity
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, Profile::class.java))
                }
                R.id.nav_chatbot -> {
                    startActivity(Intent(this, Chatbot::class.java))
                }
                R.id.nav_notifications -> {
                    startActivity(Intent(this, NotificationActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(R.color.black, R.color.white)
        swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        swipeRefreshLayout.isRefreshing = true
        Handler(Looper.getMainLooper()).postDelayed({
            currentSteps = Random.nextInt(2000) + 6000
            currentOxygen = Random.nextInt(5) + 95
            currentCalories = Random.nextInt(300) + 1500
            currentHeartRate = Random.nextInt(20) + 65
            updateProgress()
            swipeRefreshLayout.isRefreshing = false
        }, 1000)
    }

    private fun setupFabMenu() {
        addBtn.setOnClickListener {
            toggleFabMenu()
        }

        glucoseBtn.setOnClickListener {
            closeFabMenu()
            updateProgress()
        }

        bloodPressureBtn.setOnClickListener {
            closeFabMenu()
            updateProgress()
        }

        medicalRecordsBtn.setOnClickListener {
            closeFabMenu()
            startActivity(Intent(this, HealthDetails::class.java))
        }
    }

    private fun toggleFabMenu() {
        if (isFabMenuOpen) {
            closeFabMenu()
        } else {
            openFabMenu()
        }
    }

    private fun openFabMenu() {
        isFabMenuOpen = true
        glucoseBtn.show()
        bloodPressureBtn.show()
        medicalRecordsBtn.show()
        addBtn.animate().rotation(45f).setDuration(300).start()
    }

    private fun closeFabMenu() {
        isFabMenuOpen = false
        glucoseBtn.hide()
        bloodPressureBtn.hide()
        medicalRecordsBtn.hide()
        addBtn.animate().rotation(0f).setDuration(300).start()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.homepage -> true
                R.id.chatbot -> {
                    startActivity(Intent(this, Chatbot::class.java))
                    true
                }
                R.id.profile -> {
                    startActivity(Intent(this, Profile::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigation.selectedItemId = R.id.homepage
    }

    private fun setupHealthMetricsButtons() {
        findViewById<View>(R.id.steps).setOnClickListener {
            showMetricDetails("steps")
        }
        findViewById<View>(R.id.oxygen).setOnClickListener {
            showMetricDetails("oxygen")
        }
        findViewById<View>(R.id.calories).setOnClickListener {
            showMetricDetails("calories")
        }
        findViewById<View>(R.id.heartrate).setOnClickListener {
            showMetricDetails("heartrate")
        }
    }

    private fun showMetricDetails(metricType: String) {
        when (metricType) {
            "steps" -> currentSteps = Random.nextInt(2000) + 6000
            "oxygen" -> currentOxygen = Random.nextInt(5) + 95
            "calories" -> currentCalories = Random.nextInt(300) + 1500
            "heartrate" -> currentHeartRate = Random.nextInt(20) + 65
        }
        updateProgress()
    }

    private fun updateProgress() {
        val progress = calculateOverallProgress()
        progressBar.progress = progress
        textViewProgress.text = "$progress%"
    }

    private fun calculateOverallProgress(): Int {
        var totalProgress = 0
        var factors = 0

        totalProgress += (currentSteps.coerceAtMost(10000) * 100 / 10000)
        factors++

        val oxygenProgress = ((currentOxygen - 90) * 20).coerceIn(0, 100)
        totalProgress += oxygenProgress
        factors++

        totalProgress += (currentCalories.coerceAtMost(2000) * 100 / 2000)
        factors++

        val heartRateProgress = if (currentHeartRate in 60..100) 100 else 70
        totalProgress += heartRateProgress
        factors++

        return totalProgress / factors
    }

    private fun startRealTimeUpdates() {
        updateTimer = fixedRateTimer("progressUpdater", false, 0L, 30000) {
            runOnUiThread {
                when (Random.nextInt(4)) {
                    0 -> currentSteps += Random.nextInt(100)
                    1 -> currentOxygen = (currentOxygen + Random.nextInt(3) - 1).coerceIn(95, 100)
                    2 -> currentCalories += Random.nextInt(50)
                    3 -> currentHeartRate = (currentHeartRate + Random.nextInt(5) - 2).coerceIn(60, 100)
                }
                updateProgress()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateProgress()
        if (updateTimer == null) {
            startRealTimeUpdates()
        }
    }

    override fun onPause() {
        super.onPause()
        updateTimer?.cancel()
        updateTimer = null
    }

    // Button click handlers for prediction cards
    fun btndiabetes(view: View) {
        startActivity(Intent(this, PredictDiabetes::class.java))
    }

    fun btnCHD(view: View) {
        startActivity(Intent(this, PredictCHD::class.java))
    }

    fun btnAsthma(view: View) {
        startActivity(Intent(this, PredictAsthma::class.java))
    }

    fun btnStress(view: View) {
        startActivity(Intent(this, StressMngt::class.java))
    }

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.openDrawer(GravityCompat.START)
        return true
    }
}