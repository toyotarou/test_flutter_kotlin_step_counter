package com.example.test_flutter_kotlin_step_counter.util

import android.content.Context
import android.content.SharedPreferences
import java.text.SimpleDateFormat
import java.util.*

object StepDataManager {

    private const val PREF_NAME = "step_prefs"
    private const val KEY_TODAY_STEP = "today_step"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveTodayStep(context: Context, step: Int) {
        val prefs = getPrefs(context)
        prefs.edit().putInt(KEY_TODAY_STEP, step).apply()
    }

    fun loadTodayStep(context: Context): Int {
        val prefs = getPrefs(context)
        return prefs.getInt(KEY_TODAY_STEP, 0)
    }

    fun getTodayDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
