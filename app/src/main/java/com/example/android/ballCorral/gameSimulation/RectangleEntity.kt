package com.example.android.ballCorral.gameSimulation

import com.example.android.ballCorral.utility.Vector

open class RectangleEntity(val lowerBound: Vector, val upperBound: Vector) : GameEntity() {

    fun width(): Float {
        return upperBound.x - lowerBound.x
    }

    fun height(): Float {
        return upperBound.y - lowerBound.y
    }

    fun equalTo(other: RectangleEntity): Boolean {
        return ((this.lowerBound.equalTo(other.lowerBound))
                && (this.upperBound.equalTo(other.upperBound)))
    }


}