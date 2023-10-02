package com.example.afinal.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.widget.Chronometer

class AttendanceForegroundService :  Service() {
    private lateinit var chronometer: Chronometer
    private var handler: Handler = Handler()

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        chronometer = Chronometer(this)
        chronometer.base = SystemClock.elapsedRealtime()
        chronometer.start()
        handler.postDelayed(updateChronometer, 1000)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateChronometer)
        chronometer.stop()
    }

    private val updateChronometer = object : Runnable {
        override fun run() {
            // Update the chronometer every second
            // Handle overtime logic here if needed
            handler.postDelayed(this, 1000)
        }
    }
}