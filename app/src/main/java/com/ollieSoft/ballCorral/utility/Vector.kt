package com.ollieSoft.ballCorral.utility

import kotlin.math.*
import kotlin.random.Random.Default.nextDouble

class Vector(val x: Double, val y: Double) {
    var magCache: Double? = null

    val angle: Double by lazy { atan2(this.y,this.x)}

    fun cross(other: Vector): Double {
        return this.x*other.y-this.y*other.x
    }

    fun dot(other: Vector): Double {
        return this.x * other.x + this.y * other.y
    }

    fun equalTo(other: Vector): Boolean {
        return ((this.x == other.x) && (this.y == other.y))
    }

    fun mag(): Double {
        if (magCache == null) {
            magCache = sqrt(this.x.pow(2) + this.y.pow(2)) //Somewhat expensive op
        }
        return magCache!!
    }

    fun minus(other: Vector): Vector {
        return this.plus(other.times(-1.0))
    }

    fun moduloEach(other: Vector): Vector {
        return Vector(this.x.rem(other.x),this.y.rem(other.y))
    }

    fun plus(other: Vector): Vector {
        return Vector(this.x + other.x, this.y + other.y)
    }

    fun times(multiplier: Double): Vector {
        return Vector(this.x * multiplier, this.y * multiplier)
    }

    fun unitNormal(): Vector {
        if (y != 0.0) {
            return Vector(1.0, -(x / y)).unitScaled()
        }
        return Vector(0.0, 1.0)
    }

    fun unitScaled(): Vector {
        return this.times((1f) / this.mag())
    }

    companion object {

        fun randomUnit(): Vector {
            val angle: Double = (2f) * (PI.toDouble()) * nextDouble()
            return Vector(cos(angle), sin(angle))
        }

        fun randomVectorInBox(lowerBound: Vector, upperBound: Vector): Vector {
            val differenceVector = upperBound.minus(lowerBound)
            return Vector(
                lowerBound.x + nextDouble() * differenceVector.x,
                lowerBound.y + nextDouble() * differenceVector.y
            )
        }

        fun zero(): Vector {
            return Vector(0.0, 0.0)
        }
    }
}