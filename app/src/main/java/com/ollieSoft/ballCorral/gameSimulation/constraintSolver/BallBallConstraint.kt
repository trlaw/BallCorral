package com.ollieSoft.ballCorral.gameSimulation.constraintSolver

import com.example.android.ballBounce.utility.VectorN
import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.utility.Vector
import kotlin.math.min

class BallBallConstraint(private val ballA: BallEntity, private val ballB: BallEntity, idleLife: Double) :
    AbstractConstraint(idleLife) {

    private val ballBallBeta = R.string.BALL_BALL_BETA.toDouble()
    private val ballBallSlopFactor = R.string.BALL_BALL_SLOP_FACTOR.toDouble()

    var cDotInit: Double = 0.0
    var impulses = VectorN(*Array(4) { 0.0 }.toDoubleArray())

    override fun satisfied(): Boolean {
        return evalC() >= 0.0 || evalCdot() > 0.0
    }

    override fun evalC(): Double {
        return ballB.position.minus(ballA.position).mag() - (ballB.radius + ballA.radius)
    }

    override fun evalCdot(): Double {
        return deltaR().innerProduct(vN())
    }

    override fun evalImpulses(dt: Double) {
        val dR = deltaR()
        val thisC = evalC()
        val lambda =
            ((-1.0) * (evalCdot() + coeffRest() * cDotInit +
                    (ballBallBeta / dt)
                    * (if (thisC < (-1.0) * ballA.radius * ballBallSlopFactor)
                (thisC + ballA.radius * ballBallSlopFactor) else 0.0)) /
                    dR.twoNormSquared())
        impulses = dR.times(lambda)
    }

    override fun applyCorrectiveImpulses() {
        ballA.velocity =
            ballA.velocity.plus(Vector(impulses.component(1), impulses.component(3)))
        ballB.velocity =
            ballB.velocity.plus(Vector(impulses.component(0), impulses.component(2)))
        resetLifeTimeCounter()
    }

    override fun resetInitialQuantities() {
        cDotInit = evalCdot()
    }

    private fun vN(): VectorN {
        return VectorN(ballB.velocity.x, ballA.velocity.x, ballB.velocity.y, ballA.velocity.y)
    }

    private fun deltaR(): VectorN {
        val rBA = ballB.position.minus(ballA.position)
        return VectorN(rBA.x, -rBA.x, rBA.y, -rBA.y).times(1.0 / rBA.mag())
    }

    private fun coeffRest(): Double {
        return min(ballA.cOr, ballB.cOr)
    }
}