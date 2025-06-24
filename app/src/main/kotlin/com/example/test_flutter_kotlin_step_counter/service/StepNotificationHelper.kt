//package com.example.test_flutter_kotlin_step_counter.service
//
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.os.Build
//
//object StepNotificationHelper {
//
//    const val CHANNEL_ID = "step_counter_channel"
//    private const val CHANNEL_NAME = "ステップカウント通知"
//    private const val CHANNEL_DESCRIPTION = "歩数記録中に表示される通知"
//
//    fun createNotificationChannel(context: Context) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_NAME,
//                NotificationManager.IMPORTANCE_LOW
//            ).apply {
//                description = CHANNEL_DESCRIPTION
//            }
//
//            val notificationManager =
//                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
//    }
//}
