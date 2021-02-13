package com.ollieSoft.ballCorral.gameSimulation

import android.content.res.Resources
import com.ollieSoft.ballCorral.R
import kotlin.math.exp
import kotlin.math.sqrt

object GameDifficultyMap {

    private const val ballInitVmin = R.string.BALL_INIT_V_MIN.toDouble()
    private const val ballMaxVmin = R.string.BALL_MAX_V_MIN.toDouble()
    private const val ballVminExp = R.string.BALL_V_MIN_RATE_EXP.toDouble()
    private const val ballInitVmax = R.string.BALL_INIT_V_MAX.toDouble()
    private const val ballMaxVmax = R.string.BALL_MAX_V_MAX.toDouble()
    private const val ballVmaxExp = R.string.BALL_V_MAX_RATE_EXP.toDouble()
    private const val barrierDecayRate = R.string.BARRIER_DECAY_RATE.toDouble()
    private const val maxBarrierLength = R.string.MAX_BARRIER_LENGTH.toDouble()
    private var numColors = 1

    fun initializeStatics(resObj: Resources) {
        numColors = resObj.getIntArray(R.array.ballColors).size
    }

    fun maxBalls(score: Long): Long {
        return (numColors.toDouble() + sqrt(score.toDouble())).toLong()
    }

    fun playerBarrierTotalLength(score: Long): Double {
        return maxBarrierLength*exp((-1.0)* barrierDecayRate*(score.toDouble()))
    }

    fun initVmin(score: Long): Double {
        return ballMaxVmin - ((ballMaxVmin-ballInitVmin)*exp((-1.0)*ballVminExp*(score.toDouble())))
    }

    fun initVmax(score: Long): Double {
        return ballMaxVmax - ((ballMaxVmax-ballInitVmax)*exp((-1.0)*ballVmaxExp*(score.toDouble())))
    }
}