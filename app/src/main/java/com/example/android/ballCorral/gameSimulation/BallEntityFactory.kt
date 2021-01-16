package com.example.android.ballCorral.gameSimulation

const val NUM_COLORS = 4
const val BASE_COR = 1.02f
const val BASE_BALL_RADIUS = 30f

open class BallEntityFactory {
    var nextColorIndex = 0
    open fun create(): BallEntity {
        return BallEntity().apply {
            cOr = BASE_COR
            radius = BASE_BALL_RADIUS
            this.colorIndex = nextColorIndex++
            nextColorIndex %= NUM_COLORS
        }
    }

}