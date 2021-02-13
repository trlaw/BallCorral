package com.ollieSoft.ballCorral.utility

open class Rectangle(val lowerBounds: Vector, val upperBounds: Vector) {

    fun width(): Double {
        return upperBounds.x - lowerBounds.x
    }

    fun height(): Double {
        return upperBounds.y - lowerBounds.y
    }

    fun equalTo(other: Rectangle): Boolean {
        return ((this.lowerBounds.equalTo(other.lowerBounds))
                && (this.upperBounds.equalTo(other.upperBounds)))
    }

    fun isValid(): Boolean {
        return ((this.width() > 0.0) && (this.height() > 0.0))
    }

    fun randomPositionInside(): Vector {
        return Vector(
            randDoubleInRange(lowerBounds.x, upperBounds.x),
            randDoubleInRange(lowerBounds.y, upperBounds.y)
        )
    }

    companion object {
        fun zero(): Rectangle {
            return Rectangle(Vector.zero(), Vector.zero())
        }
    }
}