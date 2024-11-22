package com.example.hw

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.app.ActivityManager
import android.content.Context

class MemoryService : Service() {
    private val handler = Handler(Looper.getMainLooper())
    private val memoryInfo = ActivityManager.MemoryInfo()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMonitoring()
        return START_STICKY
    }

    private fun startMonitoring() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateMemoryInfo()
                handler.postDelayed(this, 5000)
            }
        }, 0)
    }

    private fun updateMemoryInfo() {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memoryInfo)

        val intent = Intent("MEMORY_UPDATE")
        intent.putExtra("totalMem", memoryInfo.totalMem / 1024)
        intent.putExtra("availMem", memoryInfo.availMem / 1024)
        intent.putExtra("threshold", memoryInfo.threshold / 1024)
        intent.putExtra("activeMem", memoryInfo.totalMem - memoryInfo.availMem)

        sendBroadcast(intent)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
