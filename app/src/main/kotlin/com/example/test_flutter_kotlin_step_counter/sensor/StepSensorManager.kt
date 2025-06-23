package com.example.test_flutter_kotlin_step_counter.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class StepSensorManager(
    private val context: Context,
    private val onStepChanged: (Float) -> Unit
) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepSensor: Sensor? = null

    fun start() {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepSensor != null) {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
            Log.d("StepSensorManager", "✅ センサー登録成功")
        } else {
            Log.w("StepSensorManager", "⚠️ TYPE_STEP_COUNTER が使えません")
        }
    }

    fun stop() {
        sensorManager?.unregisterListener(this)
        Log.d("StepSensorManager", "🛑 センサー登録解除")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0]
            Log.d("StepSensorManager", "🚶 歩数取得: $steps")
            onStepChanged(steps)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 今回は使わない
    }
}
