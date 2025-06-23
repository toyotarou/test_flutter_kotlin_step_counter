package com.example.test_flutter_kotlin_step_counter

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.test_flutter_kotlin_step_counter.util.StepSensorManager

class HelloStepActivity : ComponentActivity() {

    private lateinit var stepSensorManager: StepSensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stepSensorManager = StepSensorManager(this) { stepCount ->
            Log.d("HelloStepActivity", "👣 歩数: $stepCount")
            steps.value = stepCount.toInt()
        }

        checkPermissionAndStartSensor()

        setContent {
            MaterialTheme {
                val stepValue by remember { steps }
                Text(text = "👣 現在の歩数: $stepValue")
            }
        }
    }

    private fun checkPermissionAndStartSensor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        } else {
            stepSensorManager.register()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            Log.d("HelloStepActivity", "🔑 ACTIVITY_RECOGNITION granted: $isGranted")
            if (isGranted) stepSensorManager.register()
        }

    override fun onDestroy() {
        super.onDestroy()
        stepSensorManager.unregister()
    }

    companion object {
        val steps = mutableStateOf(0)
    }
}
