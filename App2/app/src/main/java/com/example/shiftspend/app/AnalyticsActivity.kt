package com.shiftspend.app

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.components.XAxis
import com.shiftspend.app.data.ExpenseRepository
import com.shiftspend.app.data.PreferencesManager
import com.shiftspend.app.data.ExpenseLog
import com.shiftspend.app.databinding.ActivityAnalyticsBinding

class AnalyticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalyticsBinding
    private lateinit var repo: ExpenseRepository
    private lateinit var prefs: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = PreferencesManager(this)
        // Tie analytics to the specific user email
        repo = ExpenseRepository(this, prefs.savedEmail)

        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }

        loadVehicleAnalytics()
    }

    private fun loadVehicleAnalytics() {
        val currentVehicle = prefs.vehicle
        val vehicleLogs = repo.getLogsForVehicle(currentVehicle)

        setupPieChart(vehicleLogs)
        setupBarChart(currentVehicle)
        setupRecyclerView(vehicleLogs)
    }

    private fun setupPieChart(logs: List<ExpenseLog>) {
        val chart = binding.pieChart

        if (logs.isEmpty()) {
            chart.clear()
            chart.setNoDataText("No data found")
            return
        }

        val categoryData = logs.groupBy { it.type }
            .mapValues { it.value.sumOf { log -> log.amount }.toFloat() }

        val entries = categoryData.map { (type, total) -> PieEntry(total, type) }

        val dataSet = PieDataSet(entries, "").apply {
            colors = listOf(Color.parseColor("#FFD700"), Color.parseColor("#DC143C"), Color.parseColor("#9A9A9A"))
            sliceSpace = 4f
            valueTextColor = Color.WHITE
            valueTextSize = 12f
        }

        chart.apply {
            data = PieData(dataSet)
            description.isEnabled = false
            legend.isEnabled = false
            setHoleColor(Color.parseColor("#0D0D0D"))
            animateY(1000, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun setupBarChart(vehicleName: String) {
        val chart = binding.barChart
        val monthlyData = repo.getMonthlyTotalsForVehicle(vehicleName)

        if (monthlyData.isEmpty()) {
            chart.clear()
            return
        }

        val months = monthlyData.keys.toTypedArray()
        val entries = monthlyData.values.mapIndexed { i, total -> BarEntry(i.toFloat(), total) }

        val dataSet = BarDataSet(entries, "").apply {
            color = Color.parseColor("#FFD700")
            valueTextColor = Color.GRAY
        }

        chart.apply {
            data = BarData(dataSet)
            description.isEnabled = false
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                valueFormatter = IndexAxisValueFormatter(months)
                textColor = Color.GRAY
                setDrawGridLines(false)
            }
            axisLeft.textColor = Color.GRAY
            axisRight.isEnabled = false
            legend.isEnabled = false
            animateY(800)
            invalidate()
        }
    }

    private fun setupRecyclerView(logs: List<ExpenseLog>) {
        binding.rvLogs.apply {
            layoutManager = LinearLayoutManager(this@AnalyticsActivity)
            adapter = ExpenseLogAdapter(logs)
        }
    }
}