package com.tagaev.myapplication.ui.parts

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import com.tagaev.myapplication.Variables
import kotlinx.coroutines.delay

@Composable
fun DrawChart(
    accelerationData: List<Double>,
    yAxisLabelCount: Int = 8, // Number of labels/markers on the Y axis
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        if (accelerationData.size < 2) return@Canvas

        val minValue = (accelerationData.minOrNull() ?: 0.0) - 1.0
        val maxValue = (accelerationData.maxOrNull() ?: 20.0) + 1.0
        val stepX = canvasWidth / (accelerationData.size - 1)

        val points = accelerationData.mapIndexed { index, value ->
            val x = index * stepX
            val y = canvasHeight - ((value - minValue) / (maxValue - minValue) * canvasHeight).toFloat()
            Offset(x, y)
        }

        for (i in 0 until points.size - 1) {
            drawLine(
                color = Color.Blue,
                start = points[i],
                end = points[i + 1],
                strokeWidth = 4f
            )
        }
        points.forEach { point ->
            drawCircle(
                color = Color.DarkGray,
                radius = 6f,
                center = point
            )
        }

        // Reserve space on left for Y-axis labels.
        val labelWidth = 50f
        val chartWidth = canvasWidth - labelWidth

        // Calculate interval for Y-axis markers.
        val valueInterval = (maxValue - minValue) / (yAxisLabelCount - 1)

        // Prepare a Paint for text drawing.
        val textPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 30f
        }
        // Draw the Y-axis labels and grid markers.
        for (i in 0 until yAxisLabelCount) {
            val value = minValue + i * valueInterval
            // Map value to y position (invert y-axis so higher values appear higher)
            val y = canvasHeight - ((value - minValue) / (maxValue - minValue) * canvasHeight).toFloat()
            // Draw text label on the left.
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawText(
                    String.format("%.1f", value),
                    0f,
                    y,
                    textPaint
                )
            }
            // Optionally, draw a horizontal grid line from the label to the chart area.
            drawLine(
                color = Color.LightGray,
                start = Offset(labelWidth, y),
                end = Offset(canvasWidth, y),
                strokeWidth = 1f
            )
        }
    }
}

@Composable
fun DynamicAccelerationChart() {
    val maxPoints = 10
    // Initialize a mutable state list with 20 initial values of 10f.
    var accelerationValues = Variables.accChangesArray //remember { mutableStateListOf<Float>().apply { repeat(20) { add(10f) } } }

    LaunchedEffect(Unit) {
        while (true) {
            delay(50L) // Update every 500ms.
            //val newValue = 10f + (Random.nextFloat() - 0.5f) * 2f
            // Remove the oldest point if we reached the maximum count.
            if (accelerationValues.size >= maxPoints) {
                accelerationValues.removeAt(0)
            }
            //accelerationValues.add(newValue)
        }
    }

    DrawChart(
        accelerationData = accelerationValues,
        modifier = Modifier.fillMaxSize()
    )
}

@Preview(showBackground = true)
@Composable
fun AccelerationChartPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        DynamicAccelerationChart()
    }
}
