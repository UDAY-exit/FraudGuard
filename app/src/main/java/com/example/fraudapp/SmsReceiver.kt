package com.example.fraudapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    private val TAG = "SmsReceiver"

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "SMS Broadcast received! Action: ${intent?.action}")

        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            Log.d(TAG, "SMS_RECEIVED_ACTION matched")

            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            messages?.forEach { sms ->
                val sender = sms.originatingAddress ?: "Unknown"
                val body = sms.messageBody ?: return@forEach

                Log.d(TAG, "Real SMS from $sender: $body")

                Toast.makeText(context, "Received SMS: $body", Toast.LENGTH_LONG).show()

                // Open SmsDetectionActivity with the message
                val launchIntent = Intent(context, SmsDetectionActivity::class.java).apply {
                    putExtra("sms_sender", sender)
                    putExtra("sms_body", body)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                context?.startActivity(launchIntent)
            }
        } else {
            Log.w(TAG, "Wrong action: ${intent?.action}")
        }
    }
}