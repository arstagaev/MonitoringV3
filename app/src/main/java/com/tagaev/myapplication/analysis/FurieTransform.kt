import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tagaev.myapplication.Variables
import com.tagaev.myapplication.Variables.accChangesArray
import com.tagaev.myapplication.Variables.analysisParameters
import org.jtransforms.fft.DoubleFFT_1D
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

fun calculateMagnitudeSpectrum(data: DoubleArray): DoubleArray {
    // Perform FFT
    val fft = DoubleFFT_1D(data.size.toLong())
    fft.realForward(data)

    // Calculate magnitude spectrum
    val magnitudeSpectrum = DoubleArray(data.size / 2 + 1)
    try {
        for (i in magnitudeSpectrum.indices-1) {
            magnitudeSpectrum[i] = sqrt(data[2 * i] * data[2 * i] + data[2 * i + 1] * data[2 * i + 1])
        }
    }catch (e: Exception) {}

    return magnitudeSpectrum
}

fun calculateFrequencyBins(sampleRate: Double, dataSize: Int): DoubleArray {
    // Calculate frequency bins
    val frequencyBins = DoubleArray(dataSize / 2 + 1)
    val deltaF = sampleRate / dataSize
    for (i in frequencyBins.indices) {
        frequencyBins[i] = i * deltaF
    }
    return frequencyBins
}

fun analysisCurrentChart(accelerationData: SnapshotStateList<Double>) {

    analysisParameters.clear()

    analysisParameters.add(Variables.AnalysisBlock("Mean",accelerationData.mean()))
    analysisParameters.add(Variables.AnalysisBlock("Standard Deviation",accelerationData.standardDeviation()))
    analysisParameters.add(Variables.AnalysisBlock("Variance",accelerationData.variance()))
    analysisParameters.add(Variables.AnalysisBlock("Median",accelerationData.median()))
}


///////

// Calculates the mean (average) of the list.
fun List<Double>.mean(): Double {
    if (this.isEmpty()) return 0.0
    return (this.sum() / this.size).roundTo(2)
}

// Calculates the sample variance (using n-1 in the denominator).
fun List<Double>.variance(): Double {
    if (this.size <= 1) return 0.0
    val mean = this.mean()
    return (this.sumByDouble { (it - mean) * (it - mean) } / (this.size - 1)).roundTo(2)
}

// Calculates the standard deviation as the square root of the variance.
fun List<Double>.standardDeviation(): Double = sqrt(this.variance()).roundTo(2)

// Optionally, calculates the median of the list.
fun List<Double>.median(): Double {
    if (this.isEmpty()) return 0.0
    val sortedList = this.sorted()
    return (if (sortedList.size % 2 == 1) {
        sortedList[sortedList.size / 2]
    } else {
        val midIndex = sortedList.size / 2
        (sortedList[midIndex - 1] + sortedList[midIndex]) / 2.0
    }).roundTo(2)
}


// Extension function to round a Double to a given number of decimals.
fun Double.roundTo(decimals: Int): Double {
    val factor = 10.0.pow(decimals)
    return round(this * factor) / factor
}