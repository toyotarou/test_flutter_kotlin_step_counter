package com.example.test_flutter_kotlin_step_counter.service

import android.content.Context
import android.content.Intent
import android.os.Build

object StepServiceManager {
    fun start(context: Context) {
        val intent = Intent(context, StepUpdateService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun stop(context: Context) {
        val intent = Intent(context, StepUpdateService::class.java)
        context.stopService(intent)
        StepUpdateService.isRunning = false
    }

    fun isRunning(context: Context): Boolean {
        return StepUpdateService.isRunning
    }
}
