package com.example.hw

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var totalMemText: TextView
    private lateinit var availMemText: TextView
    private lateinit var bufferMemText: TextView
    private lateinit var activeMemText: TextView

    private val memoryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val totalMem = intent.getLongExtra("totalMem", 0)
            val availMem = intent.getLongExtra("availMem", 0)
            val bufferMem = intent.getLongExtra("threshold", 0)
            val activeMem = intent.getLongExtra("activeMem", 0)

            totalMemText.text = "Total Memory: $totalMem KB"
            availMemText.text = "Available Memory: $availMem KB"
            bufferMemText.text = "Buffer Memory: $bufferMem KB"
            activeMemText.text = "Active Memory: $activeMem KB"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        totalMemText = findViewById(R.id.totalMemText)
        availMemText = findViewById(R.id.availMemText)
        bufferMemText = findViewById(R.id.bufferMemText)
        activeMemText = findViewById(R.id.activeMemText)

        val startServiceButton = findViewById<Button>(R.id.startServiceButton)
        val stopServiceButton = findViewById<Button>(R.id.stopServiceButton)

        startServiceButton.setOnClickListener {
            startService(Intent(this, MemoryService::class.java))
        }

        stopServiceButton.setOnClickListener {
            stopService(Intent(this, MemoryService::class.java))
        }

        registerReceiver(memoryReceiver, IntentFilter("MEMORY_UPDATE"))
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(memoryReceiver)
    }
}
