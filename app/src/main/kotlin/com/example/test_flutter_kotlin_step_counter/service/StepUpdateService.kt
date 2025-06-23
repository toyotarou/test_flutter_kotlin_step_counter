package com.example.test_flutter_kotlin_step_counter.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.test_flutter_kotlin_step_counter.db.AppDatabase
import com.example.test_flutter_kotlin_step_counter.db.StepRecord
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class StepUpdateService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = true

    override fun onCreate() {
        super.onCreate()
        startForeground(1, createNotification())
        startStepSavingLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startStepSavingLoop() {
        val dao = AppDatabase.getInstance(applicationContext).stepDao()

        serviceScope.launch {
            while (isRunning) {
                val now = System.currentTimeMillis()
                val epoch = (now / 1000L).toInt()

                val today = getToday()
                val nowTime = getTime()

                val record = dao.getByDate(today)
                if (record == null) {
                    dao.insert(StepRecord(date = today, time = nowTime, step = epoch))
                } else {
                    dao.update(record.copy(time = nowTime, step = epoch))
                }

                delay(60_000L) // 60秒待機
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = "step_service_channel"
        val channelName = "Step Counter Service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val chan = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(chan)
        }

        return Notification.Builder(this, channelId)
            .setContentTitle("歩数記録中")
            .setContentText("アプリが1分ごとに歩数を保存します")
            .setSmallIcon(android.R.drawable.ic_menu_compass)
            .build()
    }

    private fun getToday(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

}
