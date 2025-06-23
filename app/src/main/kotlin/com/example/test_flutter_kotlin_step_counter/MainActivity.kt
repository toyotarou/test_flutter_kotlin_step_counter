package com.example.test_flutter_kotlin_step_counter

import android.os.Bundle
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getInstance(applicationContext)
        val stepDao = db.stepDao()

        setContent {
            val scope = rememberCoroutineScope()
            var stepList by remember { mutableStateOf(listOf<StepRecord>()) }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    stepList = stepDao.getAll()
                }
            }

            Surface(modifier = Modifier.fillMaxSize()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Button(onClick = {
                        scope.launch(Dispatchers.IO) {
                            val today = getToday()
                            val epochSec = System.currentTimeMillis() / 1000L
                            val existing = stepDao.getByDate(today)

                            if (existing == null) {
                                stepDao.insert(StepRecord(date = today, step = epochSec.toInt()))
                            } else {
                                stepDao.update(existing.copy(step = epochSec.toInt()))
                            }

                            stepList = stepDao.getAll()
                        }
                    }) {
                        Text("🦶 今日のステップを保存/更新")
                    }

                    Button(onClick = {
                        scope.launch(Dispatchers.IO) {
                            stepDao.deleteAll()
                            stepList = stepDao.getAll()
                        }
                    }) {
                        Text("🗑 データを全削除")
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    Text("📋 保存されたステップ一覧", style = MaterialTheme.typography.titleMedium)

                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(stepList) { record ->
                            Text("📅 ${record.date} ：👣 ${record.step} 歩")
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
}
