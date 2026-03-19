package com.example.fraudapp

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fraudapp.adapter.MessageAdapter
import com.example.fraudapp.data.DataRepository
import com.example.fraudapp.databinding.ActivityHistoryBinding
import com.example.fraudapp.model.MessageModel

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var adapter: MessageAdapter
    private var allMessages = listOf<MessageModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = "Message History"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        allMessages = DataRepository.analyzedHistory.toList()

        setupRecyclerView()
        setupFilterSpinner()
        updateEmptyState()
    }

    private fun setupRecyclerView() {
        adapter = MessageAdapter(allMessages)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = this@HistoryActivity.adapter
        }
    }

    private fun setupFilterSpinner() {
        val filterOptions = listOf("All Messages", "Fraud Only", "Safe Only")
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, filterOptions)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilter.adapter = spinnerAdapter

        binding.spinnerFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val filtered = when (position) {
                    1 -> allMessages.filter { it.isFraud }
                    2 -> allMessages.filter { !it.isFraud }
                    else -> allMessages
                }
                adapter.updateData(filtered)
                updateEmptyState()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateEmptyState() {
        if (allMessages.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.tvEmptyState.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.tvEmptyState.visibility = View.GONE
        }
        binding.tvTotalCount.text = "Total: ${allMessages.size} messages"
        binding.tvFraudCount.text = "Fraud: ${allMessages.count { it.isFraud }}"
        binding.tvSafeCount.text = "Safe: ${allMessages.count { !it.isFraud }}"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}
