package com.ollieSoft.ballCorral.utility

import android.view.Surface
import kotlin.random.Random

fun randDoubleInRange(DoubleMin: Double, DoubleMax: Double): Double {
    return DoubleMin + Random.nextDouble() * (DoubleMax - DoubleMin)
}

fun rotateSensorToDisplayCoords(rotConstant: Int, inputVector: Vector): Vector? {
    return when (rotConstant) {
        Surface.ROTATION_0 -> inputVector
        Surface.ROTATION_90 -> Vector(-1f * inputVector.y, inputVector.x)
        Surface.ROTATION_180 -> inputVector.times(-1.0)
        Surface.ROTATION_270 -> Vector(inputVector.y, -1f * inputVector.x)
        else -> null
    }
}

fun inClosedInterval(lowerBound: Double, upperBound: Double, testValue: Double): Boolean {
    return ((testValue >= lowerBound) && (testValue <= upperBound))
}

fun inClosedRectangle(lowerBounds: Vector, upperBounds: Vector, testPosition: Vector): Boolean {
    return (inClosedInterval(lowerBounds.x, upperBounds.x, testPosition.x)
            && inClosedInterval(lowerBounds.y, upperBounds.y, testPosition.y))
}

fun invokeAllPairs(collectionSize: Int, funToInvoke: (Int, Int) -> Unit) {
    for (i in 0 until collectionSize) {
        for (j in 0 until i) {
            funToInvoke(i, j)
        }
    }
}

fun invokeAllOrderedPairs(indexOneSize: Int, indexTwoSize: Int, funToInvoke: (Int, Int) -> Unit) {
    for (i in 0 until indexOneSize) {
        for (j in 0 until indexTwoSize) {
            funToInvoke(i, j)
        }
    }
}

fun vectorMidpoint(v1: Vector, v2: Vector): Vector {
    return Vector((v1.x + v2.x) / (2f), (v1.y + v2.y) / (2f))
}