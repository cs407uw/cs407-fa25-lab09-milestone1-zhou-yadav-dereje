package com.cs407.lab09

/**
 * Represents a ball that can move. (No Android UI imports!)
 *
 * Constructor parameters:
 * - backgroundWidth: the width of the background, of type Float
 * - backgroundHeight: the height of the background, of type Float
 * - ballSize: the width/height of the ball, of type Float
 */
class Ball(
    private val backgroundWidth: Float,
    private val backgroundHeight: Float,
    private val ballSize: Float
) {
    var posX = 0f
    var posY = 0f
    var velocityX = 0f
    var velocityY = 0f
    private var accX = 0f
    private var accY = 0f

    private var isFirstUpdate = true

    init {
        reset() //always start centered
    }

    /**
     * Updates the ball's position and velocity based on the given acceleration and time step.
     * (See lab handout for physics equations)
     */
//    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {
//        if(isFirstUpdate) {
//            isFirstUpdate = false
//            accX = xAcc
//            accY = yAcc
//            return
//        }
//
//        accX = xAcc
//        accY = yAcc
//        velocityX += accX * dT
//        velocityY += accY * dT
//        posX += velocityX * dT
//        posY += velocityY * dT
//        checkBoundaries() //prevents ball from leaving the screen
//
//    }
    fun updatePositionAndVelocity(xAcc: Float, yAcc: Float, dT: Float) {
        if(isFirstUpdate) {
            isFirstUpdate = false
            accX = xAcc
            accY = yAcc
            return
        }

        // Store previous acceleration
        val prevAccX = accX
        val prevAccY = accY

        // Update current acceleration
        accX = xAcc
        accY = yAcc

        // Update velocity using Equation 1: v1 = v0 + 0.5 * (a1 + a0) * dt
        velocityX += 0.5f * (accX + prevAccX) * dT
        velocityY += 0.5f * (accY + prevAccY) * dT

        // Update position using Equation 2: l = v0 * dt + (1/6) * dt^2 * (3*a0 + a1)
        posX += velocityX * dT + (1f/6f) * dT * dT * (3f * prevAccX + accX)
        posY += velocityY * dT + (1f/6f) * dT * dT * (3f * prevAccY + accY)

        checkBoundaries()
    }

    /**
     * Ensures the ball does not move outside the boundaries.
     * When it collides, velocity and acceleration perpendicular to the
     * boundary should be set to 0.
     */
    fun checkBoundaries() {
        //left wall
        if (posX < 0f) {
            posX = 0f //return to left
            velocityX = 0f
            accX = 0f
        }

        //right wall
        val rightLimit = backgroundWidth - ballSize
        if (posX > rightLimit) {
            posX = rightLimit //return to right
            velocityX = 0f
            accX = 0f
        }

        //top wall
        if (posY < 0f) {
            posY = 0f  //return to top
            velocityY = 0f
            accY = 0f
        }

        //bottom
        val bottomLimit = backgroundHeight - ballSize
        if (posY > bottomLimit) {
            posY = bottomLimit  //return to bottom
            velocityY = 0f
            accY = 0f
        }


    }

    /**
     * Resets the ball to the center of the screen with zero
     * velocity and acceleration.
     */
    fun reset() {
        //center ball
        posX = (backgroundWidth - ballSize) / 2f
        posY = (backgroundHeight - ballSize) / 2f

        velocityX = 0f
        velocityY = 0f
        accX = 0f
        accY = 0f

        isFirstUpdate = true //next update should reinitialize acceleration

    }
}