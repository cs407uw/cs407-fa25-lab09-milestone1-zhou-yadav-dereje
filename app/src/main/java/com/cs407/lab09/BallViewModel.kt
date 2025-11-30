package com.cs407.lab09

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BallViewModel : ViewModel() {

    private var ball: Ball? = null
    private var lastTimestamp: Long = 0L

    private var calibrationX = 0f
    private var calibrationY = 0f
    private var isCalibrated = false
    private val calibrationSamples = mutableListOf<Pair<Float, Float>>()
    private val calibrationSampleCount = 20

    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        if (ball == null) {
            ball =
                    Ball(
                            backgroundWidth = fieldWidth,
                            backgroundHeight = fieldHeight,
                            ballSize = ballSizePx
                    )

            _ballPosition.value = Offset(x = ball!!.posX, y = ball!!.posY)
        }
    }

    fun onSensorDataChanged(event: SensorEvent) {
        val currentBall = ball ?: return

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            val rawX = event.values[0]
            val rawY = event.values[1]
            val rawZ = event.values[2]

            if (!isCalibrated) {
                calibrationSamples.add(Pair(rawX, rawY))
                if (calibrationSamples.size >= calibrationSampleCount) {
                    calibrationX = calibrationSamples.map { it.first }.average().toFloat()
                    calibrationY = calibrationSamples.map { it.second }.average().toFloat()
                    isCalibrated = true
                    Log.d("BallViewModel", "Calibrated: X=$calibrationX, Y=$calibrationY")
                }
                return
            }

            if (lastTimestamp != 0L) {
                val nS2S = 1.0f / 1_000_000_000.0f
                val dT = (event.timestamp - lastTimestamp) * nS2S

                val correctedX = rawX - calibrationX
                val correctedY = rawY - calibrationY

                val threshold = 0.3f

                val xAcc = if (kotlin.math.abs(correctedX) < threshold) 0f else -correctedX
                val yAcc = if (kotlin.math.abs(correctedY) < threshold) 0f else correctedY

                currentBall.updatePositionAndVelocity(xAcc = xAcc, yAcc = yAcc, dT = dT)

                _ballPosition.update { Offset(currentBall.posX, currentBall.posY) }
            }

            lastTimestamp = event.timestamp
        }
    }

    fun reset() {
        ball?.reset()

        ball?.let { centeredBall ->
            _ballPosition.value = Offset(centeredBall.posX, centeredBall.posY)
        }

        lastTimestamp = 0L
    }
}
