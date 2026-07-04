package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class SynthAudioPlayer {
    private val sampleRate = 44100
    private val scope = CoroutineScope(Dispatchers.Default)

    fun playClick() {
        scope.launch {
            try {
                val durationSec = 0.08
                val numSamples = (sampleRate * durationSec).toInt()
                val sample = FloatArray(numSamples)
                
                // Frequency sweeps down rapidly
                val startFreq = 1200.0
                val endFreq = 400.0
                
                var phase = 0.0
                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val ratio = i.toDouble() / numSamples
                    val freq = startFreq + (endFreq - startFreq) * ratio
                    // Add a slight decay envelope
                    val envelope = 1.0 - ratio
                    phase += 2.0 * Math.PI * freq / sampleRate
                    sample[i] = (sin(phase) * envelope * 0.4f).toFloat()
                }
                
                playRawBuffer(sample)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playLevelUp() {
        scope.launch {
            try {
                // Ascending minor triad chord sweep (C4 -> Eb4 -> G4 -> C5)
                val notes = doubleArrayOf(261.63, 311.13, 392.00, 523.25)
                val durationSec = 0.15
                val numSamplesPerNote = (sampleRate * durationSec).toInt()
                val totalSamples = numSamplesPerNote * notes.size
                val sample = FloatArray(totalSamples)

                for (n in notes.indices) {
                    val freq = notes[n]
                    var phase = 0.0
                    for (i in 0 until numSamplesPerNote) {
                        val index = n * numSamplesPerNote + i
                        val ratio = i.toDouble() / numSamplesPerNote
                        val envelope = if (ratio < 0.2) ratio / 0.2 else (1.0 - ratio)
                        phase += 2.0 * Math.PI * freq / sampleRate
                        // Mix a secondary square-ish overtone for a retro arcade 8-bit aesthetic
                        val primary = sin(phase)
                        val overtone = if (sin(phase * 2.0) > 0) 0.15 else -0.15
                        sample[index] = ((primary + overtone) * envelope * 0.35f).toFloat()
                    }
                }

                playRawBuffer(sample)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playPenaltyAlarm() {
        scope.launch {
            try {
                // Heavy ominous pulsing drone - dual detuned frequencies
                val durationSec = 1.8
                val numSamples = (sampleRate * durationSec).toInt()
                val sample = FloatArray(numSamples)

                val freq1 = 55.0  // low A
                val freq2 = 56.5  // detuned
                
                var phase1 = 0.0
                var phase2 = 0.0
                for (i in 0 until numSamples) {
                    val ratio = i.toDouble() / numSamples
                    val lfo = sin(2.0 * Math.PI * 2.0 * ratio) * 0.5 + 0.5 // 2Hz volume pulse
                    phase1 += 2.0 * Math.PI * freq1 / sampleRate
                    phase2 += 2.0 * Math.PI * freq2 / sampleRate
                    
                    val signal = sin(phase1) + sin(phase2)
                    // Fade out envelope at the very end
                    val envelope = if (ratio > 0.8) (1.0 - ratio) / 0.2 else 1.0
                    sample[i] = (signal * lfo * envelope * 0.5f).toFloat()
                }

                playRawBuffer(sample)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun playRawBuffer(buffer: FloatArray) {
        val bufferSize = buffer.size * 4 // 4 bytes per float
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_FLOAT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size, AudioTrack.WRITE_NON_BLOCKING)
        audioTrack.play()
        
        // Wait and release
        scope.launch {
            val waitTime = (buffer.size.toDouble() / sampleRate * 1000).toLong() + 200
            kotlinx.coroutines.delay(waitTime)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (e: Exception) {
                // already released
            }
        }
    }
}
