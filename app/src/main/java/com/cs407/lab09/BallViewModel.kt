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

    // Expose the ball's position as a StateFlow
    private val _ballPosition = MutableStateFlow(Offset.Zero)
    val ballPosition: StateFlow<Offset> = _ballPosition.asStateFlow()

    /**
     * Called by the UI when the game field's size is known.
     */
    fun initBall(fieldWidth: Float, fieldHeight: Float, ballSizePx: Float) {
        if (ball == null) {
            ball = Ball(backgroundWidth = fieldWidth,
                backgroundHeight = fieldHeight,
                ballSize = ballSizePx)

            _ballPosition.value = Offset(
                x = ball!!.posX,
                y = ball!!.posY
            )
        }
    }

    /**
     * Called by the SensorEventListener in the UI.
     */
    fun onSensorDataChanged(event: SensorEvent) {
        // Ensure ball is initialized
        val currentBall = ball ?: return

        if (event.sensor.type == Sensor.TYPE_GRAVITY) {
            if (lastTimestamp != 0L) {
                val NS2S = 1.0f / 1_000_000_000.0f
                val dT = (event.timestamp - lastTimestamp) * NS2S

                val rawX = event.values[0]
                val rawY = event.values[1]
                val rawZ = event.values[2]

                val threshold = 0.03f //reducing drift when phone is stable

//                val xAcc = -rawX
//                val yAcc = rawY
                val xAcc = if (kotlin.math.abs(rawX) < threshold) 0f else -rawX
                val yAcc = if (kotlin.math.abs(rawY) < threshold) 0f else rawY

                currentBall.updatePositionAndVelocity(
                    xAcc = xAcc,
                    yAcc = yAcc,
                    dT = dT
                )

                _ballPosition.update {
                    Offset(
                        currentBall.posX,
                        currentBall.posY
                    )
                }
            }

            lastTimestamp = event.timestamp
        }
    }

    fun reset() {
        ball?.reset()

        ball?.let { centeredBall ->
            _ballPosition.value = Offset(
                centeredBall.posX,
                centeredBall.posY
            )
        }

        lastTimestamp = 0L
    }
}