package com.example.fraudapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.fraudapp.databinding.ActivityResultBinding
import com.example.fraudapp.model.MessageModel
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import java.util.Locale

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Analysis Result"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val message: MessageModel? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("MESSAGE_MODEL", MessageModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("MESSAGE_MODEL") as? MessageModel
        }

        if (message == null) {
            finish()
            return
        }

        displayResult(message)
    }

    private fun displayResult(message: MessageModel) {
        binding.tvSmsContent.text = message.content
        binding.tvFraudProbability.text = String.format(Locale.US, "%.1f%%", message.fraudProbability)
        binding.tvTimestamp.text = "Analyzed at: ${message.timestamp}"
        binding.tvSender.text = "From: ${message.sender}"

        val fraudColor: Int
        val safeColor: Int = Color.parseColor("#00C853")
        val redColor: Int = Color.parseColor("#FF5252")

        if (message.isFraud) {
            fraudColor = redColor
            binding.cardResult.setCardBackgroundColor(Color.parseColor("#FFEBEE"))
            binding.tvDetectionResult.text = "\u26A0 FRAUD DETECTED"
            binding.tvDetectionResult.setTextColor(Color.parseColor("#D32F2F"))
            binding.tvResultDescription.text =
                "This message shows strong indicators of financial fraud. " +
                        "Do NOT click any links or provide personal information."
            binding.tvResultDescription.setTextColor(Color.parseColor("#B71C1C"))
            binding.imgResultIcon.setImageResource(android.R.drawable.ic_dialog_alert)
            binding.dividerResult.setBackgroundColor(Color.parseColor("#D32F2F"))
            binding.probabilityBar.progressTintList = ColorStateList.valueOf(redColor)
        } else {
            fraudColor = safeColor
            binding.cardResult.setCardBackgroundColor(Color.parseColor("#E8F5E9"))
            binding.tvDetectionResult.text = "\u2713 SAFE MESSAGE"
            binding.tvDetectionResult.setTextColor(Color.parseColor("#2E7D32"))
            binding.tvResultDescription.text =
                "This message appears to be legitimate. " +
                        "No significant fraud indicators were detected."
            binding.tvResultDescription.setTextColor(Color.parseColor("#1B5E20"))
            binding.imgResultIcon.setImageResource(android.R.drawable.checkbox_on_background)
            binding.dividerResult.setBackgroundColor(safeColor)
            binding.probabilityBar.progressTintList = ColorStateList.valueOf(safeColor)
        }

        binding.probabilityBar.progress = message.fraudProbability.toInt()

        setupGaugeChart(message.fraudProbability, fraudColor)

        val fadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in)
        binding.cardResult.startAnimation(fadeIn)

        binding.btnViewHistory.setOnClickListener {
            val navIntent = Intent(this, HistoryActivity::class.java)
            navIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(navIntent)
        }

        binding.btnAnalyzeAnother.setOnClickListener {
            finish()
        }
    }

    private fun setupGaugeChart(probability: Float, accentColor: Int) {
        val remainingColor: Int = Color.parseColor("#E0E0E0")

        // Clamp so no slice is ever 0 (MPAndroidChart crashes with 0-value slices)
        val clamped: Float = probability.coerceIn(0.5f, 99.5f)
        val remaining: Float = 100f - clamped

        val entries: List<PieEntry> = listOf(
            PieEntry(clamped, "Risk"),
            PieEntry(remaining, "")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(accentColor, remainingColor)
        dataSet.setDrawValues(false)
        dataSet.sliceSpace = 2f

        val pieData = PieData(dataSet)

        binding.gaugeChart.apply {
            data = pieData
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 70f
            transparentCircleRadius = 72f
            setHoleColor(Color.WHITE)
            setCenterText(String.format(Locale.US, "%.0f%%\nRisk", probability))
            setCenterTextSize(16f)
            setCenterTextColor(accentColor)
            legend.isEnabled = false
            isRotationEnabled = false
            setTouchEnabled(false)
            animateY(1000, Easing.EaseInOutQuad)
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