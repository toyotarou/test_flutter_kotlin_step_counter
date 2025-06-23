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
    }

    fun isRunning(context: Context): Boolean {
        val activityManager =
            context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val services = activityManager.getRunningServices(Int.MAX_VALUE)
        return services.any { it.service.className == StepUpdateService::class.qualifiedName }
    }

}
