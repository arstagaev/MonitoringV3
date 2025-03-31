package com.tagaev.myapplication.analysis

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

import android.content.Context
import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp

// A simple Complex class.
data class Complex(val re: Double, val im: Double) {
    operator fun plus(other: Complex) = Complex(re + other.re, im + other.im)
    operator fun minus(other: Complex) = Complex(re - other.re, im - other.im)
    operator fun times(other: Complex) = Complex(
        re * other.re - im * other.im,
        re * other.im + im * other.re
    )
    fun magnitude() = sqrt(re * re + im * im)
}

// Recursive Cooley-Tukey FFT (input size must be a power of 2).
fun fft(x: List<Complex>): List<Complex> {
    val n = x.size
    if (n == 1) return x
    if (n % 2 != 0) throw IllegalArgumentException("Size of input must be a power of 2")

    // Split into even and odd elements.
    val even = fft(x.filterIndexed { index, _ -> index % 2 == 0 })
    val odd = fft(x.filterIndexed { index, _ -> index % 2 != 0 })
    val combined = MutableList(n) { Complex(0.0, 0.0) }
    for (k in 0 until n / 2) {
        // Compute the twiddle factor.
        val exp = Complex(cos(-2 * PI * k / n), sin(-2 * PI * k / n))
        combined[k] = even[k] + exp * odd[k]
        combined[k + n / 2] = even[k] - exp * odd[k]
    }
    return combined
}

// Pads the input data with zeros so that its length is the next power of two.
fun padToPowerOfTwo(data: List<Double>): List<Double> {
    var m = 1
    while (m < data.size) m *= 2
    return data + List(m - data.size) { 0.0 }
}

// Converts a raw array of real data into an FFT magnitude spectrum (curve array).
fun convertToFFTCurve(data: List<Double>): List<Double> {
    // Pad data if necessary.
    val paddedData = padToPowerOfTwo(data)
    // Convert the real data into a list of Complex numbers.
    val complexData = paddedData.map { Complex(it, 0.0) }
    // Compute the FFT.
    val fftResult = fft(complexData)
    // For real input, the meaningful data is in the first half of the FFT result.
    val halfSize = fftResult.size / 2
    // Map each complex value to its magnitude.
    return fftResult.take(halfSize).map { it.magnitude() }
}

/////




/**
 * FFTChart draws a line chart for FFT data (frequency, value) and displays scales for both axes.
 *
 * @param fftData List of Pair(frequency, value). For example, each pair could be (index as frequency, acceleration value).
 * @param modifier Modifier for sizing and layout.
 */
@Composable
fun FFTChart(
    fftData: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier
) {
    if (fftData.isEmpty()) {
        Text("No FFT data available", modifier = modifier)
        return
    }

    // Determine bounds for scaling the data points.
    val minX = fftData.minOf { it.first }
    val maxX = fftData.maxOf { it.first }
    val minY = fftData.minOf { it.second }
    val maxY = fftData.maxOf { it.second }

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Draw X axis at the bottom.
        drawLine(
            color = Color.Gray,
            start = Offset(0f, height),
            end = Offset(width, height),
            strokeWidth = 2f
        )
        // Draw Y axis on the left.
        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(0f, height),
            strokeWidth = 2f
        )

        // Set up tick parameters.
        val tickCount = 5 // Number of intervals on each axis.
        val tickSize = 10f

        // Create a native Paint instance to draw text labels.
        val textPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 30f
        }
        val nativeCanvas = drawContext.canvas.nativeCanvas

        // Draw X axis ticks and labels (frequency).
        for (i in 0..tickCount) {
            val x = i * (width / tickCount)
            // Calculate frequency value for this tick.
            val freqValue = minX + i * (maxX - minX) / tickCount
            // Draw a short vertical tick mark.
            drawLine(
                color = Color.Gray,
                start = Offset(x, height),
                end = Offset(x, height - tickSize),
                strokeWidth = 2f
            )
            // Draw the frequency label below the tick.
            // Adjust the x position slightly to center the text.
            nativeCanvas.drawText(
                String.format("%.1f", freqValue),
                x - 15f,
                height - 15f,
                textPaint
            )
        }

        // Draw Y axis ticks and labels (value).
        for (i in 0..tickCount) {
            val y = height - i * (height / tickCount)
            // Calculate value for this tick.
            val value = minY + i * (maxY - minY) / tickCount
            // Draw a short horizontal tick mark.
            drawLine(
                color = Color.Gray,
                start = Offset(0f, y),
                end = Offset(tickSize, y),
                strokeWidth = 2f
            )
            // Draw the value label to the right of the tick.
            nativeCanvas.drawText(
                String.format("%.1f", value),
                tickSize + 5f,
                y + 10f,
                textPaint
            )
        }

        // Build the path for the FFT data line.
        val path = Path().apply {
            fftData.forEachIndexed { index, point ->
                // Map data point to canvas coordinates.
                val x = if (maxX - minX != 0f) ((point.first - minX) / (maxX - minX)) * width else 0f
                // Invert y coordinate as 0 is at the top.
                val y = if (maxY - minY != 0f) height - ((point.second - minY) / (maxY - minY)) * height else height
                if (index == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        // Draw the FFT line using the primary theme color.
        drawPath(path = path, color = Color.Blue)
    }
}

// FFTAlertDialog accepts an accelerationData array. In this example, each data point is converted
// into a Pair, where the x-value is the index (which could represent a frequency bin) and the y-value is the acceleration.
@Composable
fun FFTAlertDialog(
    accelerationData: List<Float>,
    onDismiss: () -> Unit
) {
    // Map the accelerationData into FFT data points.
    val fftData = accelerationData.mapIndexed { index, value ->
        // Here, the index is used as the x-axis value (frequency) and the acceleration value as y.
        index.toFloat() to value
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("FFT Chart") },
        text = {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item {
                    FFTChart(
                        fftData = fftData,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Frequency (index) vs Acceleration",
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onDismiss
            }) {
                Text("Close")
            }
        }
    )
}