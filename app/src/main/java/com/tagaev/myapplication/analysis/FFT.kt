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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import org.jtransforms.fft.DoubleFFT_1D
import kotlin.math.hypot

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

    // Determine the frequency and magnitude bounds.
    val minFreq = fftData.minOf { it.first }
    val maxFreq = fftData.maxOf { it.first }
    val minMag = fftData.minOf { it.second }
    val maxMag = fftData.maxOf { it.second }

    Canvas(modifier = modifier) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        // Draw X axis (frequency) at the bottom and Y axis (magnitude) on the left.
        drawLine(
            color = Color.Gray,
            start = Offset(0f, canvasHeight),
            end = Offset(canvasWidth, canvasHeight),
            strokeWidth = 2f
        )
        drawLine(
            color = Color.Gray,
            start = Offset(0f, 0f),
            end = Offset(0f, canvasHeight),
            strokeWidth = 2f
        )

        // Set up tick parameters.
        val tickCount = 5
        val tickLength = 10f

        // Create a native Paint instance to draw text labels.
        val textPaint = Paint().apply {
            color = android.graphics.Color.BLACK
            textSize = 30f
        }
        val nativeCanvas = drawContext.canvas.nativeCanvas

        // Draw frequency ticks (X axis)
        for (i in 0..tickCount) {
            val x = i * (canvasWidth / tickCount)
            // Compute corresponding frequency value.
            val freq = minFreq + i * (maxFreq - minFreq) / tickCount
            drawLine(
                color = Color.Gray,
                start = Offset(x, canvasHeight),
                end = Offset(x, canvasHeight - tickLength),
                strokeWidth = 2f
            )
            nativeCanvas.drawText(
                "${freq.toInt()} Hz",
                x - 20f,
                canvasHeight - 15f,
                textPaint
            )
        }

        // Draw magnitude ticks (Y axis)
        for (i in 0..tickCount) {
            val y = canvasHeight - i * (canvasHeight / tickCount)
            val mag = minMag + i * (maxMag - minMag) / tickCount
            drawLine(
                color = Color.Gray,
                start = Offset(0f, y),
                end = Offset(tickLength, y),
                strokeWidth = 2f
            )
            nativeCanvas.drawText(
                String.format("%.1f", mag),
                tickLength + 5f,
                y + 10f,
                textPaint
            )
        }

        // Calculate the width of each bar (with some spacing).
        val barWidth = (canvasWidth / fftData.size) * 0.8f

        // Draw FFT data as vertical bars.
        fftData.forEachIndexed { index, (frequency, magnitude) ->
            // X position based on index.
            val xCenter = (index + 0.5f) * (canvasWidth / fftData.size)
            // Map the magnitude to the canvas height.
            val barHeight = if (maxMag - minMag != 0f) {
                ((magnitude - minMag) / (maxMag - minMag)) * canvasHeight
            } else {
                0f
            }
            drawRect(
                color = Color.Magenta,
                topLeft = Offset(xCenter - barWidth / 2, canvasHeight - barHeight),
                size = Size(barWidth, barHeight)
            )
        }
    }
}


/**
 * Computes FFT data from the given accelerationData.
 *
 * @param accelerationData List of Float values representing your time-domain signal.
 * @param sampleFreq The sampling frequency in Hz.
 * @return A list of Pair where the first element is the frequency (Hz) and the second is the magnitude.
 */
fun calculateFFTData(
    accelerationData: List<Float>,
    sampleFreq: Float
): List<Pair<Float, Float>> {
    val n = accelerationData.size
    // Convert the input list to a DoubleArray.
    val realData = DoubleArray(n) { i -> accelerationData[i].toDouble() }

    // Create an FFT instance from JTransforms.
    val fft = DoubleFFT_1D(n.toLong())
    // Compute the FFT in-place on the realData array.
    // After this call, the data is in a packed format.
    fft.realForward(realData)

    // The FFT result for a real signal is stored as:
    // realData[0] = DC component (real)
    // For k = 1 to n/2-1:
    //   realData[2*k]   = real part of frequency bin k
    //   realData[2*k+1] = imaginary part of frequency bin k
    // If n is even, realData[n] (or realData[n-1]) contains the Nyquist frequency component (real only).

    val fftData = mutableListOf<Pair<Float, Float>>()

    // DC component (k = 0)
    val dcMag = kotlin.math.abs(realData[0])
    fftData.add(0f to dcMag.toFloat())

    // Loop over frequency bins 1 to n/2 - 1.
    val halfN = n / 2
    for (k in 1 until halfN) {
        val realPart = realData[2 * k]
        val imagPart = realData[2 * k + 1]
        // Magnitude of the k-th frequency component.
        val mag = hypot(realPart, imagPart)
        // Frequency corresponding to this bin.
        val freq = k * sampleFreq / n.toFloat()
        fftData.add(freq to mag.toFloat())
    }

    // If n is even, handle the Nyquist frequency bin.
    if (n % 2 == 0) {
        val nyquistMag = kotlin.math.abs(realData[n - 1])
        val nyquistFreq = sampleFreq / 2
        fftData.add(nyquistFreq to nyquistMag.toFloat())
    }

    return fftData
}


// FFTAlertDialog accepts an accelerationData array. In this example, each data point is converted
// into a Pair, where the x-value is the index (which could represent a frequency bin) and the y-value is the acceleration.
@Composable
fun FFTAlertDialog(
    accelerationData: List<Float>,
    onDismiss: () -> Unit
) {
    // Map the accelerationData into FFT data points.
//    val fftData = accelerationData.mapIndexed { index, value ->
//        // Here, the index is used as the x-axis value (frequency) and the acceleration value as y.
//        index.toFloat() to value
//    }

    val fftData = calculateFFTData(accelerationData,1f)

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
