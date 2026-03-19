package com.example.fraudapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.fraudapp.data.DataRepository
import com.example.fraudapp.databinding.ActivityDashboardBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Dashboard"
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        updateDashboardStats()
        setupPieChart()
        setupBarChart()

        // Navigation clicks
        binding.cardSmsDetection.setOnClickListener {
            startActivity(Intent(this, SmsDetectionActivity::class.java))
        }

        binding.cardHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        binding.fabAnalyze.setOnClickListener {
            startActivity(Intent(this, SmsDetectionActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateDashboardStats()
        setupPieChart()
        setupBarChart()
        Toast.makeText(this, "Dashboard refreshed – analyzed: ${DataRepository.analyzedHistory.size}", Toast.LENGTH_SHORT).show()
    }

    private fun updateDashboardStats() {
        val history = DataRepository.analyzedHistory

        val total = history.size
        val fraudCount = history.count { it.isFraud }
        val safeCount = total - fraudCount
        val fraudRate = if (total > 0) (fraudCount * 100f / total) else 0f

        // Update counters (always real, starts at 0)
        binding.tvTotalAnalyzed.text = total.toString()
        binding.tvFraudDetected.text = fraudCount.toString()
        binding.tvSafeMessages.text = safeCount.toString()
        binding.tvFraudRate.text = String.format("%.1f%%", fraudRate)

        // Toggle no-data message
        binding.tvNoData.visibility = if (total == 0) View.VISIBLE else View.GONE
    }

    private fun setupPieChart() {
        val history = DataRepository.analyzedHistory
        val total = history.size
        val fraudCount = history.count { it.isFraud }
        val safeCount = total - fraudCount

        val entries = if (total == 0) {
            listOf(PieEntry(1f, "No Data"))
        } else {
            listOf(
                PieEntry(fraudCount.toFloat(), "Fraud"),
                PieEntry(safeCount.toFloat(), "Safe")
            )
        }

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = if (total == 0) {
            listOf(Color.parseColor("#B0BEC5"))
        } else {
            listOf(Color.parseColor("#FF5252"), Color.parseColor("#00C853"))
        }
        dataSet.valueTextColor = Color.WHITE
        dataSet.valueTextSize = 14f
        dataSet.sliceSpace = 3f

        val pieData = PieData(dataSet)
        binding.pieChart.apply {
            data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 58f
            transparentCircleRadius = 61f
            setHoleColor(Color.WHITE)
            setCenterText(if (total == 0) "No Data" else "Fraud\nAnalysis")
            setCenterTextSize(14f)
            setCenterTextColor(if (total == 0) Color.parseColor("#757575") else Color.parseColor("#1A237E"))
            legend.textSize = 12f
            legend.textColor = Color.parseColor("#424242")
            animateY(1200, Easing.EaseInOutQuad)
            invalidate()
        }
    }

    private fun setupBarChart() {
        val history = DataRepository.analyzedHistory.take(7).reversed()

        if (history.isEmpty()) {
            val entries = listOf(BarEntry(0f, 0f))
            val dataSet = BarDataSet(entries, "No Data")
            dataSet.color = Color.parseColor("#B0BEC5")

            val barData = BarData(dataSet)
            binding.barChart.apply {
                data = barData
                description.text = "No messages analyzed"
                description.textSize = 12f
                xAxis.valueFormatter = IndexAxisValueFormatter(listOf("No Data"))
                xAxis.granularity = 1f
                axisLeft.axisMinimum = 0f
                axisLeft.axisMaximum = 100f
                axisRight.isEnabled = false
                legend.isEnabled = false
                animateY(1000)
                invalidate()
            }
            return
        }

        val barEntries = history.mapIndexed { index, msg ->
            BarEntry(index.toFloat(), msg.fraudProbability)
        }

        val labels = history.map { it.timestamp.substring(11, 16) }

        val dataSet = BarDataSet(barEntries, "Fraud Probability (%)")
        dataSet.colors = history.map { msg ->
            if (msg.isFraud) Color.parseColor("#FF5252") else Color.parseColor("#00C853")
        }
        dataSet.valueTextSize = 9f
        dataSet.valueTextColor = Color.parseColor("#424242")

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f

        binding.barChart.apply {
            data = barData
            description.text = "Recent Message Analysis"
            description.textSize = 10f
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.granularity = 1f
            xAxis.labelRotationAngle = -30f
            xAxis.textSize = 9f
            axisLeft.axisMinimum = 0f
            axisLeft.axisMaximum = 100f
            axisRight.isEnabled = false
            legend.textSize = 11f
            setFitBars(true)
            animateY(1000)
            invalidate()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}