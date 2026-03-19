package com.example.fraudapp.data

import com.example.fraudapp.model.MessageModel

object DataRepository {

    val sampleMessages = listOf(
        "URGENT: Your bank account has been compromised! Click http://fake-bank.com to verify immediately or account will be blocked.",
        "Hi! Are you coming to dinner tonight? Let me know if you need a ride.",
        "Congratulations! You've won $5,000,000 in our lottery. Send your bank details to claim your prize NOW!",
        "Your OTP for transaction is 847291. Valid for 10 minutes. Do not share with anyone.",
        "FREE OFFER: Claim your free iPhone 15 by clicking this link: bit.ly/scam123. Limited time only!",
        "Meeting rescheduled to 3 PM tomorrow. Please confirm your attendance.",
        "ALERT: Suspicious login detected on your account. Verify at http://phishing-site.net immediately!",
        "Your Amazon order #123-456 has been shipped. Expected delivery: Tomorrow by 8 PM.",
        "You have been selected for a special credit card offer. 0% interest. Call now: 1-800-FAKE.",
        "Mom: Don't forget to pick up milk on your way home. Thanks!"
    )

    val analyzedHistory = mutableListOf(
        MessageModel(1, sampleMessages[0], true, 97.3f, "2024-01-15 09:23:11", "+1-900-FRAUD"),
        MessageModel(2, sampleMessages[1], false, 4.1f, "2024-01-15 10:05:44", "+1-555-2847"),
        MessageModel(3, sampleMessages[2], true, 99.1f, "2024-01-15 11:17:32", "LOTTERY"),
        MessageModel(4, sampleMessages[3], false, 6.8f, "2024-01-15 12:00:00", "HDFC-BANK"),
        MessageModel(5, sampleMessages[4], true, 95.6f, "2024-01-15 13:45:22", "PROMO"),
        MessageModel(6, sampleMessages[5], false, 2.3f, "2024-01-15 14:10:09", "+1-555-9034"),
        MessageModel(7, sampleMessages[6], true, 98.7f, "2024-01-15 15:33:51", "ALERT-SVC"),
        MessageModel(8, sampleMessages[7], false, 1.5f, "2024-01-15 16:20:17", "AMAZON"),
        MessageModel(9, sampleMessages[8], true, 89.4f, "2024-01-15 17:08:43", "+1-800-3253"),
        MessageModel(10, sampleMessages[9], false, 3.2f, "2024-01-15 18:55:29", "MOM")
    )

    fun getTotalAnalyzed() = analyzedHistory.size

    fun getFraudCount() = analyzedHistory.count { it.isFraud }

    fun getFraudRate(): Float {
        if (analyzedHistory.isEmpty()) return 0f
        return (getFraudCount().toFloat() / getTotalAnalyzed().toFloat()) * 100f
    }

    fun analyzeMessage(content: String): MessageModel {
        val fraudKeywords = listOf(
            "urgent", "click", "verify", "bank account", "compromised", "congratulations",
            "won", "lottery", "prize", "claim", "free", "offer", "alert", "suspicious",
            "phishing", "blocked", "immediately", "limited time", "selected", "credit card",
            "send your bank", "OTP", "password", "account blocked", "winner", "million"
        )

        val lowerContent = content.lowercase()
        var fraudScore = 0f

        fraudKeywords.forEach { keyword ->
            if (lowerContent.contains(keyword.lowercase())) {
                fraudScore += when (keyword) {
                    "bank account", "compromised", "phishing", "send your bank" -> 25f
                    "urgent", "immediately", "blocked" -> 15f
                    "won", "lottery", "prize", "winner", "million" -> 20f
                    "congratulations", "free", "offer", "selected" -> 10f
                    else -> 8f
                }
            }
        }

        val urlPattern = Regex("http[s]?://\\S+|bit\\.ly/\\S+|www\\.\\S+")
        if (urlPattern.containsMatchIn(content)) {
            fraudScore += 20f
        }

        val phonePattern = Regex("1-800|1-900|\\+1-[0-9]{3}-[A-Z]{4}")
        if (phonePattern.containsMatchIn(content)) {
            fraudScore += 10f
        }

        fraudScore = fraudScore.coerceAtMost(99.9f)

        val isFraud = fraudScore >= 40f

        val timestamp = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
            .format(java.util.Date())

        val newMessage = MessageModel(
            id = analyzedHistory.size + 1,
            content = content,
            isFraud = isFraud,
            fraudProbability = if (fraudScore < 5f) (2f..15f).random() else fraudScore,
            timestamp = timestamp,
            sender = "MANUAL_INPUT"
        )

        analyzedHistory.add(0, newMessage)
        return newMessage
    }

    private fun ClosedRange<Float>.random(): Float =
        (Math.random() * (endInclusive - start) + start).toFloat()
}
