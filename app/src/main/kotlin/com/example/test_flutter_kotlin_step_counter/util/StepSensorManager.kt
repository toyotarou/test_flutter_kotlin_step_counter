package com.example.test_flutter_kotlin_step_counter.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class StepSensorManager(
    private val context: Context,
    private val onStepUpdate: (Float) -> Unit
) : SensorEventListener {

    private val TAG = "StepSensorManager"

    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null

    fun register() {
        Log.d(TAG, "✅ register() called")

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        if (sensorManager == null) {
            Log.e(TAG, "❌ SensorManager is null")
            return
        }

        stepCounterSensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        if (stepCounterSensor == null) {
            Log.e(TAG, "❌ StepCounterSensor not available")
            return
        }

        val success = sensorManager!!.registerListener(
            this,
            stepCounterSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        Log.d(
            TAG,
            if (success) "✅ Step sensor registered successfully" else "❌ Failed to register step sensor"
        )
    }

    fun unregister() {
        Log.d(TAG, "🛑 unregister() called")
        sensorManager?.unregisterListener(this)
        sensorManager = null
        stepCounterSensor = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            Log.w(TAG, "⚠️ onSensorChanged called with null event")
            return
        }

        if (event.sensor.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0]
            Log.d(TAG, "🚶 onSensorChanged - StepCounter value: $steps")
            try {
                onStepUpdate(steps)
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception in onStepUpdate: ${e.message}", e)
            }
        } else {
            Log.w(TAG, "⚠️ Unexpected sensor type: ${event.sensor.type}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "📡 Sensor accuracy changed: $accuracy for sensor: ${sensor?.name}")
    }
}
