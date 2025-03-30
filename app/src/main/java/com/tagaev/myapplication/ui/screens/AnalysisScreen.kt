package com.tagaev.myapplication.ui.screens

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arstagaev.flowble.gentelman_kit.logInfo
import com.tagaev.myapplication.ui.parts.DrawChart
import kotlinx.coroutines.delay
import java.io.File

@Composable
fun AnalysisScreen() {
    // State holding the list of files
    val files = remember { mutableStateListOf<File>() }
    // Currently selected file (if any)
    var selectedFile by remember { mutableStateOf<File?>(null) }
    // Acceleration values parsed from the file (first value from each line)
    val accelerationData = remember { mutableStateListOf<Double>() }

    // Load the list of files from the monitoring folder when this screen starts.
    LaunchedEffect(Unit) {
        // Get the downloads directory
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        // Create monitoring directory
        val monitoringDir = File(downloadsDir, "monitoring")
        if (monitoringDir.exists() && monitoringDir.isDirectory) {
            files.clear()
            files.addAll(monitoringDir.listFiles()?.toList() ?: emptyList())
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .background(Color.LightGray)) {
        Text("Select a file to parse:", modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(files) { file ->
                Text(
                    text = file.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            selectedFile = file
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedFile != null) {
            // When a file is selected, parse it step-by-step.
            LaunchedEffect(selectedFile) {
                accelerationData.clear() // reset previous data
                selectedFile?.bufferedReader()?.useLines { lines ->
                    lines.forEach { line ->
                        // Expected format: "9999.0;9999.0;9999.0;1743332936761"
                        val tokens = line.split(";")
                        if (tokens.isNotEmpty()) {
                            logInfo("START PARSE!!!")
                            val value = tokens[0].toDoubleOrNull() ?: 0.0
                            accelerationData.add(value)
                            // Simulate step-by-step parsing with a delay.
                            delay(2)
                        } else {
                           logInfo("TOKENS EMPTY")
                        }
                    }
                }
            }
            // Pass the parsed acceleration data to the chart drawing function.
            DrawChart(accelerationData = accelerationData.toList(), modifier = Modifier.fillMaxSize().weight(3f))
        }
    }
}

