package com.example.test_flutter_kotlin_step_counter.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import com.example.test_flutter_kotlin_step_counter.db.AppDatabase
import com.example.test_flutter_kotlin_step_counter.db.StepRecord
import com.example.test_flutter_kotlin_step_counter.util.StepDataManager
import com.example.test_flutter_kotlin_step_counter.util.StepSensorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StepUpdateService : Service() {
    private val TAG = "StepUpdateService"
    private var stepSensorManager: StepSensorManager? = null

    companion object {
        @Volatile
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "🟢 onCreate() called")
        isRunning = true

        // Foreground Service の通知設定
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "step_update_channel"
            val channel = NotificationChannel(
                channelId,
                "Step Update Service",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            Log.d(TAG, "🔔 NotificationChannel 作成")

            val notification: Notification = Notification.Builder(this, channelId)
                .setContentTitle("歩数記録中")
                .setContentText("センサーから歩数を記録しています")
                .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                .build()

            startForeground(1, notification)
            Log.d(TAG, "📢 Foreground通知開始")
        }

        // センサー登録と保存処理
        stepSensorManager = StepSensorManager(this) { steps ->
            Log.d(TAG, "🚶 歩数更新イベント: steps=$steps")
            StepDataManager.saveTodayStep(this, steps.toInt())

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val today = StepDataManager.getTodayDate()
                    val nowTime = StepDataManager.getCurrentTime()
                    val dao = AppDatabase.getDatabase(applicationContext).stepDao()
                    Log.d(TAG, "📅 今日の日付: $today, 現在時刻: $nowTime")

                    val existing = dao.getByDate(today)
                    if (existing != null) {
                        val updated = existing.copy(step = steps.toInt(), time = nowTime)
                        dao.update(updated)
                        Log.d(TAG, "📝 DB更新成功: $updated")
                    } else {
                        val record = StepRecord(date = today, time = nowTime, step = steps.toInt())
                        dao.insert(record)
                        Log.d(TAG, "🆕 DB新規挿入成功: $record")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "❌ DB保存処理中にエラー: ${e.message}", e)
                }
            }
        }

        Log.d(TAG, "📡 センサー登録開始")
        stepSensorManager?.register()
    }

    override fun onDestroy() {
        Log.d(TAG, "🔴 onDestroy() called")
        stepSensorManager?.unregister()
        isRunning = false
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "🔗 onBind() called -> null返却")
        return null
    }
}
