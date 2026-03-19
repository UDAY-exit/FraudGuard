package com.example.fraudapp.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fraudapp.ResultActivity
import com.example.fraudapp.databinding.ItemMessageBinding
import com.example.fraudapp.model.MessageModel

class MessageAdapter(
    private var messages: List<MessageModel>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    inner class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageModel) {
            binding.tvMessageContent.text = message.content
            binding.tvTimestamp.text = message.timestamp
            binding.tvSender.text = "From: ${message.sender}"
            binding.tvFraudProbability.text = String.format("%.1f%% risk", message.fraudProbability)

            if (message.isFraud) {
                binding.tvDetectionBadge.text = "FRAUD"
                binding.tvDetectionBadge.setBackgroundColor(Color.parseColor("#FF5252"))
                binding.tvDetectionBadge.setTextColor(Color.WHITE)
                binding.cardMessage.setCardBackgroundColor(Color.parseColor("#FFF8F8"))
                binding.viewColorStrip.setBackgroundColor(Color.parseColor("#FF5252"))
                binding.tvFraudProbability.setTextColor(Color.parseColor("#D32F2F"))
            } else {
                binding.tvDetectionBadge.text = "SAFE"
                binding.tvDetectionBadge.setBackgroundColor(Color.parseColor("#00C853"))
                binding.tvDetectionBadge.setTextColor(Color.WHITE)
                binding.cardMessage.setCardBackgroundColor(Color.parseColor("#F8FFF8"))
                binding.viewColorStrip.setBackgroundColor(Color.parseColor("#00C853"))
                binding.tvFraudProbability.setTextColor(Color.parseColor("#2E7D32"))
            }

            binding.root.setOnClickListener {
                val intent = Intent(binding.root.context, ResultActivity::class.java)
                intent.putExtra("MESSAGE_MODEL", message)
                binding.root.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size

    fun updateData(newMessages: List<MessageModel>) {
        messages = newMessages
        notifyDataSetChanged()
    }
}
