package com.example.fraudapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.fraudapp.data.DataRepository
import com.example.fraudapp.databinding.ActivitySmsDetectionBinding
import com.example.fraudapp.model.MessageModel
import com.example.fraudapp.network.ApiClient
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class SmsDetectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySmsDetectionBinding
    private var currentSmsIndex = 0
    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newSingleThreadExecutor()
    private val SMS_PERMISSION_REQUEST_CODE = 1001
    private val TAG = "SmsDetection"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySmsDetectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "SMS Detection"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        handleIncomingSms(intent)

        if (binding.tvSmsContent.text.isEmpty() ||
            binding.tvSmsContent.text == "Loading SMS...") {
            loadNextSms()
        }

        checkSmsPermissions()

        binding.btnNextSms.setOnClickListener {
            loadNextSms()
        }

        binding.btnAnalyze.setOnClickListener {
            val messageContent = binding.tvSmsContent.text.toString().trim()
            if (messageContent.isEmpty() || messageContent == "Loading SMS...") {
                Toast.makeText(this, "No message to analyze", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            startAnalysis(messageContent)
        }

        binding.btnCustomMessage.setOnClickListener {
            val customText = binding.etCustomMessage.text.toString().trim()
            if (customText.isEmpty()) {
                binding.etCustomMessage.error = "Enter a message to analyze"
                return@setOnClickListener
            }
            binding.tvSmsContent.text = customText
            binding.tvSender.text = "Sender: CUSTOM_INPUT"
            binding.tvTimestamp.text = "Just now"
        }
    }

    private fun checkSmsPermissions() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.RECEIVE_SMS,
                    Manifest.permission.READ_SMS
                ),
                SMS_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "SMS permission granted!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "SMS permission denied.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun handleIncomingSms(intent: Intent?) {
        val incomingBody = intent?.getStringExtra("sms_body")?.trim()
        val incomingSender = intent?.getStringExtra("sms_sender") ?: "Unknown"

        if (!incomingBody.isNullOrEmpty()) {
            Log.d(TAG, "Real SMS received: $incomingBody")
            binding.tvSmsContent.text = incomingBody
            binding.tvSender.text = "Sender: $incomingSender"
            binding.tvTimestamp.text = "Received: just now"
            startAnalysis(incomingBody)
        }
    }

    private fun loadNextSms() {
        val messages = DataRepository.sampleMessages
        val senders = listOf(
            "+1-900-FRAUD", "+1-555-2847", "LOTTERY", "HDFC-BANK", "PROMO",
            "+1-555-9034", "ALERT-SVC", "AMAZON", "+1-800-3253", "MOM"
        )
        binding.tvSmsContent.text = messages[currentSmsIndex]
        binding.tvSender.text = "Sender: ${senders[currentSmsIndex]}"
        binding.tvTimestamp.text = "Received: just now"
        currentSmsIndex = (currentSmsIndex + 1) % messages.size
    }

    private fun startAnalysis(content: String) {
        Log.d(TAG, "startAnalysis: $content")

        binding.progressAnalyzing.visibility = View.VISIBLE
        binding.tvAnalyzingStatus.visibility = View.VISIBLE
        binding.tvAnalyzingStatus.text = "Analyzing message..."
        binding.btnAnalyze.isEnabled = false
        binding.btnNextSms.isEnabled = false

        executor.execute {
            try {
                Log.d(TAG, "Calling API...")
                val prediction = ApiClient.predict(content)
                Log.d(TAG, "API result - isFraud: ${prediction.isFraud}")

                val timestamp = SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).format(Date())

                val messageModel = MessageModel(
                    id = DataRepository.analyzedHistory.size + 1,
                    content = content,
                    isFraud = prediction.isFraud,
                    fraudProbability = prediction.fraudProbability,
                    timestamp = timestamp,
                    sender = binding.tvSender.text.toString()
                        .removePrefix("Sender: ")
                )

                DataRepository.analyzedHistory.add(0, messageModel)

                handler.post {
                    hideLoading()
                    val resultIntent = Intent(this, ResultActivity::class.java)
                    resultIntent.putExtra("MESSAGE_MODEL", messageModel)
                    startActivity(resultIntent)
                }

            } catch (e: Exception) {
                Log.e(TAG, "API error: ${e.message}", e)
                handler.post {
                    hideLoading()
                    Toast.makeText(
                        this,
                        "Cannot connect to server. Make sure API is running and you are on the same WiFi.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun hideLoading() {
        binding.progressAnalyzing.visibility = View.GONE
        binding.tvAnalyzingStatus.visibility = View.GONE
        binding.btnAnalyze.isEnabled = true
        binding.btnNextSms.isEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        executor.shutdown()
    }
}