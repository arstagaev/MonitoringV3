package com.tagaev.myapplication.ui.screens

import analysisCurrentChart
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arstagaev.flowble.gentelman_kit.logInfo
import com.tagaev.myapplication.Variables.analysisParameters
import com.tagaev.myapplication.analysis.FFTAlertDialog
import com.tagaev.myapplication.getConfigParameter
import com.tagaev.myapplication.ui.ConfigDialogJson
import com.tagaev.myapplication.ui.parts.DrawChart
import com.tagaev.myapplication.ui.theme.textWhiterColor2
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

// Dummy logger
fun logInfo(message: String) {
    println(message)
}

// A data class for feature parameters (e.g. Kalman Filter On/Off)
data class AnalysisParameter(val name: String, val value: String)

@Composable
fun AnalysisScreen() {
    // State holding the list of files.
    val files = remember { mutableStateListOf<File>() }
    // Currently selected file (if any).
    var selectedFile by remember { mutableStateOf<File?>(null) }
    // Acceleration values parsed from the file (first value from each line).
    val showingData = remember { mutableStateListOf<Double>() }
    val originalFileData = remember { mutableStateListOf<Double>() }

    // List of features (for now only one: Kalman Filter).
    val configureChartTool = remember {
        mutableStateListOf(
            AnalysisParameter("Kalman Filter", "Off"),
            AnalysisParameter("fast Fourier transform (FFT)", "Off"),
            AnalysisParameter("Settings", "")
            // Add more features here as needed.
        )
    }
    val analysisParametersInternal = remember { analysisParameters }
    // Mutable state to control the dialog visibility.
    var showDialog by remember { mutableStateOf(false) }
    var showDialogFFT by remember { mutableStateOf(false) }

    var numberOfLines = 0

    val context = LocalContext.current
    val crScope = rememberCoroutineScope()

    // Load the list of files from the monitoring folder when this screen starts.
    LaunchedEffect(Unit) {
        // Get the downloads directory.
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        // Create monitoring directory.
        val monitoringDir = File(downloadsDir, "monitoring")
        if (monitoringDir.exists() && monitoringDir.isDirectory) {
            files.clear()
            files.addAll(monitoringDir.listFiles()?.toList() ?: emptyList())
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 3.dp)
        .background(Color.LightGray)) {
        Text("Select a file to parse:", modifier = Modifier.padding(bottom = 8.dp))
        LazyColumn(modifier = Modifier.weight(2f)) {
            items(files) { file ->
                Text(
                    text = file.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                        .clickable {
                            crScope.launch {
                                selectedFile = file
                            }
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (selectedFile != null) {
            // When a file is selected, parse it step-by-step.
            LaunchedEffect(selectedFile) {
                showingData.clear() // reset previous data
                selectedFile?.bufferedReader()?.useLines { lines ->
                    lines.forEach { line ->
                        // Expected format: "9999.0;9999.0;9999.0;1743332936761"
                        val tokens = line.split(";")
                        if (tokens.isNotEmpty()) {
                            logInfo("START PARSE!!!")
                            val value = tokens[0].toDoubleOrNull() ?: 0.0
                            showingData.add(value)
                            originalFileData.add(value)
                            // Simulate step-by-step parsing with a short delay.
                            delay(1)
                        } else {
                            logInfo("TOKENS EMPTY")
                        }

                    }

                }
                analysisCurrentChart(showingData)

            }
            // Draw the chart with the current acceleration data.
            DrawChart(
                accelerationData = showingData.toList(),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(5f)
            )
            // Feature list: a row of buttons to modify the chart.
            LazyRow(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                items(configureChartTool) { param ->
                    Box(
                        modifier = Modifier
                            .background(Color.Gray, shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                            .padding(5.dp)
                            .clickable {
                                if (param.name == "Kalman Filter") {
                                    // Toggle the Kalman Filter: if Off, apply it; if On, revert.
                                    if (param.value == "Off") {
                                        val filteredData = applyKalmanFilter(showingData.toList())
                                        showingData.clear()
                                        showingData.addAll(filteredData)
                                        // Update the feature's state.
                                        val index = configureChartTool.indexOf(param)
                                        configureChartTool[index] = param.copy(value = "On")
                                    } else {
                                        // For a real app you might want to reload the original file;
                                        // here we simply mark the feature as off.
                                        val index = configureChartTool.indexOf(param)
                                        configureChartTool[index] = param.copy(value = "Off")

                                        showingData.clear()
                                        showingData.addAll(originalFileData)
                                    }
                                }

                                if (param.name == "fast Fourier transform (FFT)") {
                                    // Toggle the Kalman Filter: if Off, apply it; if On, revert.
                                    showDialogFFT = true
//                                    if (param.value == "Off") {
//                                        val fftCurve = convertToFFTCurve(showingData)
//
//                                        showingData.clear()
//                                        showingData.addAll(fftCurve)
//                                        // Update the feature's state.
//                                        val index = configureChartTool.indexOf(param)
//                                        configureChartTool[index] = param.copy(value = "On")
//
//
//                                    } else {
//                                        // For a real app you might want to reload the original file;
//                                        // here we simply mark the feature as off.
//                                        val index = configureChartTool.indexOf(param)
//                                        configureChartTool[index] = param.copy(value = "Off")
//
//                                        showingData.clear()
//                                        showingData.addAll(originalFileData)
//                                    }
                                }
                                if (param.name == "Settings") {
                                    showDialog = true
                                    getConfigParameter(context,"")
                                }
                            }
                    ) {
                        if (param.name == "Settings") {
                            Icon(modifier = Modifier.align(Alignment.Center),imageVector = Icons.Filled.Settings, contentDescription = "settings")
                        } else {
                            Text("${param.name}\n${param.value}", fontSize = 12.sp, color = Color.White)
                        }

                    }
                }
            }
            LazyRow(
                modifier = Modifier.fillMaxSize().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                items(analysisParametersInternal) {
                    Box(
                        modifier = Modifier.background(
                            Color.DarkGray,
                            shape = RoundedCornerShape(10.dp)
                        ).padding(5.dp)
                    ) {
                        Text("${it.name}\n${it.value}", color = textWhiterColor2)
                    }
                }
            }
        }

        // Conditionally show the dialog based on the state.
        if (showDialog) {
            ConfigDialogJson(
                context = context,
                onDismiss = { showDialog = false }
            )
        }
        if (showDialogFFT) {
            FFTAlertDialog(originalFileData.toList().map { it.toFloat() },
                onDismiss = { showDialogFFT = false }
            )
        }
    }
}

// Dummy implementation for updating chart after file parsing.
fun analysisCurrentChart(accelerationData: MutableList<Double>) {
    // Update the chart using the new accelerationData array.
    // This could include computing statistics, etc.
    logInfo("Chart updated with ${accelerationData.size} points")
}

// A simple Kalman Filter implementation for a list of Double values.
fun applyKalmanFilter(data: List<Double>): List<Double> {
    if (data.isEmpty()) return emptyList()
    val filtered = mutableListOf<Double>()
    var estimate = data.first()
    var errorCovariance = 1.0
    val processNoise = 1e-5
    val measurementNoise = 1e-2

    for (measurement in data) {
        // Prediction update.
        errorCovariance += processNoise
        // Compute Kalman gain.
        val kalmanGain = errorCovariance / (errorCovariance + measurementNoise)
        // Update the estimate.
        estimate += kalmanGain * (measurement - estimate)
        // Update the error covariance.
        errorCovariance = (1 - kalmanGain) * errorCovariance
        filtered.add(estimate)
    }
    return filtered
}

// Placeholder for DrawChart composable.
@Composable
fun DrawChart(
    accelerationData: List<Double>,
    yAxisLabelCount: Int = 8, // Number of labels/markers on the Y axis
    modifier: Modifier = Modifier,
) {
    // Replace with your actual chart drawing logic.
    Column(modifier = modifier.padding(16.dp)) {
        Text("Chart Data:", modifier = Modifier.padding(bottom = 8.dp))
        accelerationData.forEach { value ->
            Text(text = value.toString())
        }
    }
}

