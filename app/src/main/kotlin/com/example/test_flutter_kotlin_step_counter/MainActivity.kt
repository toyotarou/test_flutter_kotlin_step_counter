package com.example.test_flutter_kotlin_step_counter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.test_flutter_kotlin_step_counter.db.AppDatabase
import com.example.test_flutter_kotlin_step_counter.db.StepRecord
import com.example.test_flutter_kotlin_step_counter.service.StepServiceManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = AppDatabase.getInstance(applicationContext).stepDao()

        setContent {
            val scope = rememberCoroutineScope()
            var stepList by remember { mutableStateOf(listOf<StepRecord>()) }
            var secondsLeft by remember { mutableStateOf(60) }

            // 初期読み込み
            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    stepList = dao.getAll()
                }
            }

            LaunchedEffect(Unit) {
                while (true) {
                    delay(1000)
                    secondsLeft = (secondsLeft - 1).coerceAtLeast(0)
                    if (secondsLeft == 0) {
                        secondsLeft = 60
                        val newList = withContext(Dispatchers.IO) {
                            dao.getAll()
                        }
                        stepList = newList // ← これが重要
                    }
                }
            }

            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    // 手動保存ボタン
                    Button(onClick = {
                        scope.launch(Dispatchers.IO) {
                            val today = getToday()
                            val nowTime = getTime()
                            val epochSec = System.currentTimeMillis() / 1000L
                            val existing = dao.getByDate(today)

                            if (existing == null) {
                                dao.insert(
                                    StepRecord(
                                        date = today,
                                        time = nowTime,
                                        step = epochSec.toInt()
                                    )
                                )
                            } else {
                                dao.update(existing.copy(time = nowTime, step = epochSec.toInt()))
                            }

                            stepList = dao.getAll()
                        }
                    }) {
                        Text("🦶 今日のステップを保存/更新")
                    }

                    // 全削除ボタン
                    Button(onClick = {
                        scope.launch(Dispatchers.IO) {
                            dao.deleteAll()
                            stepList = dao.getAll()
                        }
                    }) {
                        Text("🗑 データを全削除")
                    }

                    // ForegroundService 開始
                    Button(onClick = {
                        StepServiceManager.start(this@MainActivity)
                        Toast.makeText(this@MainActivity, "▶️ サービス開始", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                        Text("▶️ ステップ記録サービス開始")
                    }

                    // ForegroundService 停止
                    Button(onClick = {
                        StepServiceManager.stop(this@MainActivity)
                        Toast.makeText(this@MainActivity, "⏹ サービス停止", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                        Text("⏹ ステップ記録サービス停止")
                    }

                    // サービス状態確認
                    Button(onClick = {
                        val isRunning = StepServiceManager.isRunning(this@MainActivity)
                        Toast.makeText(
                            this@MainActivity,
                            if (isRunning) "✅ サービス稼働中" else "❌ サービス停止中",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                        Text("❓ サービス状態確認")
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        "⏳ 次の保存まで：${secondsLeft} 秒",
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("📋 保存されたステップ一覧", style = MaterialTheme.typography.titleMedium)

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(stepList) { record ->
                            Text("📅 ${record.date} ⏰ ${record.time} ：👣 ${record.step} 歩")
                        }
                    }
                }
            }
        }
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
