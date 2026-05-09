package com.shiftspend.app

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.shiftspend.app.data.ExpenseLog
import com.shiftspend.app.data.ExpenseRepository
import com.shiftspend.app.data.PreferencesManager
import com.shiftspend.app.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var prefs: PreferencesManager
    private lateinit var repo: ExpenseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)
        repo = ExpenseRepository(this, prefs.savedEmail)

        loadDashboard()

        binding.btnSettings.setOnClickListener { showSettingsDialog() }
        binding.btnViewAnalytics.setOnClickListener {
            startActivity(Intent(this, AnalyticsActivity::class.java))
            overridePendingTransition(R.anim.fade_in, android.R.anim.fade_out)
        }
        binding.fabAdd.setOnClickListener { showAddExpenseDialog() }
        binding.btnLogFuel.setOnClickListener { showAddExpenseDialog("Fuel") }
        binding.btnMenu.setOnClickListener { showMenuOptions() }
    }

    override fun onResume() {
        super.onResume()
        loadDashboard()
    }

    private fun loadDashboard() {
        val currency = prefs.getCurrencySymbol()
        val units = prefs.getDistanceUnit()
        val currentVehicle = prefs.vehicle

        binding.tvVehicleName.text = currentVehicle.uppercase(Locale.getDefault())

        val vehicleTotal = repo.getTotalForVehicle(currentVehicle)
        binding.tvTotalSpend.text = "${String.format(Locale.getDefault(), "%,.0f", vehicleTotal)} $currency"

        // MATH: Calculate remaining distance for Service
        val remaining = prefs.serviceTarget - prefs.currentMileage

        // Update Next Service Text
        if (remaining <= 0) {
            binding.tvNextService.text = "SERVICE DUE"
            binding.tvNextService.setTextColor(ContextCompat.getColor(this, R.color.crimson))
        } else {
            binding.tvNextService.text = "$remaining $units"
            binding.tvNextService.setTextColor(ContextCompat.getColor(this, R.color.white))
        }

        // UPDATE LIVE ALERTS (Text below the static 3D car)
        updateMaintenanceAlerts(remaining)
    }

    private fun updateMaintenanceAlerts(remaining: Int) {
        // Tire Alert Logic
        if (remaining < 500) {
            binding.tvTireStatus.text = "Tires: Needs replacement"
            binding.tvTireStatus.setTextColor(ContextCompat.getColor(this, R.color.crimson))
            binding.ivTireIndicator.setBackgroundResource(R.drawable.bg_wear_alert)
        } else {
            binding.tvTireStatus.text = "Tires: Good"
            binding.tvTireStatus.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.ivTireIndicator.setBackgroundResource(R.drawable.bg_wear_ok)
        }

        // Engine Alert Logic
        if (remaining < 1000) {
            binding.tvEngineStatus.text = "Engine: Due soon"
            binding.tvEngineStatus.setTextColor(ContextCompat.getColor(this, R.color.gold))
            binding.ivEngineIndicator.setBackgroundResource(R.drawable.bg_wear_alert)
        } else {
            binding.tvEngineStatus.text = "Engine: Healthy"
            binding.tvEngineStatus.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding.ivEngineIndicator.setBackgroundResource(R.drawable.bg_wear_ok)
        }
    }

    private fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)
        val dialog = AlertDialog.Builder(this, R.style.DarkGlassDialog).setView(dialogView).create()

        dialog.show()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val spinnerVehicle = dialogView.findViewById<Spinner>(R.id.spinner_vehicle)
        val etOdometer = dialogView.findViewById<EditText>(R.id.et_current_odometer)
        val etServiceTarget = dialogView.findViewById<EditText>(R.id.et_service_target)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save_settings)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btn_close_settings)

        etOdometer.setText(prefs.currentMileage.toString())
        etServiceTarget.setText(prefs.serviceTarget.toString())

        val vehicles = arrayOf("Daily Sedan", "Pit Bike", "SUV", "Motorcycle")
        val vAdapter = ArrayAdapter(this, R.layout.spinner_item, vehicles)
        vAdapter.setDropDownViewResource(R.layout.spinner_item)
        spinnerVehicle.adapter = vAdapter
        spinnerVehicle.setSelection(vehicles.indexOf(prefs.vehicle).coerceAtLeast(0))

        btnClose?.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            prefs.vehicle = spinnerVehicle.selectedItem.toString()
            prefs.currentMileage = etOdometer.text.toString().toIntOrNull() ?: prefs.currentMileage
            prefs.serviceTarget = etServiceTarget.text.toString().toIntOrNull() ?: prefs.serviceTarget

            Toast.makeText(this, "Garage Updated ✓", Toast.LENGTH_SHORT).show()
            loadDashboard()
            dialog.dismiss()
        }
    }

    private fun showAddExpenseDialog(preselectedType: String? = null) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_expense, null)
        val dialog = AlertDialog.Builder(this, R.style.DarkGlassDialog).setView(dialogView).create()

        dialog.show()
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        dialog.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val spinner = dialogView.findViewById<Spinner>(R.id.spinner_type)
        val etAmount = dialogView.findViewById<EditText>(R.id.et_amount)
        val btnSave = dialogView.findViewById<Button>(R.id.btn_save_expense)
        val btnCancel = dialogView.findViewById<Button>(R.id.btn_cancel)

        val types = arrayOf("Fuel", "Parts", "Repairs", "Oil Change")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, types)
        adapter.setDropDownViewResource(R.layout.spinner_item)
        spinner.adapter = adapter

        if (preselectedType != null) {
            val index = types.indexOf(preselectedType)
            if (index != -1) spinner.setSelection(index)
        }

        btnCancel?.setOnClickListener { dialog.dismiss() }

        btnSave.setOnClickListener {
            val amount = etAmount.text.toString().toDoubleOrNull() ?: 0.0
            if (amount <= 0) return@setOnClickListener

            val log = ExpenseLog(
                type = spinner.selectedItem.toString(),
                amount = amount,
                currency = prefs.getCurrencySymbol(),
                vehicle = prefs.vehicle,
                notes = "",
                date = SimpleDateFormat("MMM dd", Locale.getDefault()).format(Date())
            )
            repo.saveExpense(log)
            dialog.dismiss()
            loadDashboard()
        }
    }

    private fun showInfoGuide() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_info_guide, null)
        val dialog = AlertDialog.Builder(this, R.style.DarkGlassDialog).setView(dialogView).create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialogView.findViewById<Button>(R.id.btn_got_it)?.setOnClickListener { dialog.dismiss() }
    }

    private fun logout() {
        prefs.isLoggedIn = false
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun showMenuOptions() {
        val options = arrayOf("Home", "Analytics", "Info Guide", "Logout")
        AlertDialog.Builder(this, R.style.DarkGlassDialog)
            .setTitle("MENU")
            .setItems(options) { _, which ->
                when (which) {
                    1 -> startActivity(Intent(this, AnalyticsActivity::class.java))
                    2 -> showInfoGuide()
                    3 -> logout()
                }
            }.show()
    }
}