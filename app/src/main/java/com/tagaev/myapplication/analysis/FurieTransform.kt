import org.jtransforms.fft.DoubleFFT_1D
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