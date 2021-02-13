package com.ollieSoft.ballCorral.gameSimulation.constraintSolver

import com.example.android.ballBounce.utility.VectorN
import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.GameState
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.utility.Vector
import kotlin.math.abs
import kotlin.math.min

class BallBallConstraint(
    private val ballA: BallEntity,
    private val ballB: BallEntity,
    idleLife: Double
) :
    AbstractConstraint(idleLife) {

    private val ballBallBeta = R.string.BALL_BALL_BETA.toDouble()
    private val ballBallSlopFactor = R.string.BALL_BALL_SLOP_FACTOR.toDouble()

    var cDotInit: Double = 0.0
    var impulses = VectorN(*Array(4) { 0.0 }.toDoubleArray())
    var ballAshadowPos = Vector.zero()

    override fun satisfied(): Boolean {
        return evalC() >= 0.0 || evalCdot() > 0.0
    }

    override fun evalC(): Double {
        return ballB.position.minus(ballAshadowPos).mag() - (ballB.radius + ballA.radius)
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

    override fun resetInitialQuantities(gameState: GameState) {
        computeShadowPos(gameState) //Wrap position for one ball as needed
        cDotInit = evalCdot()
    }

    private fun computeShadowPos(gameState: GameState) {
        var ballAshadowPosX = ballA.position.x
        var ballAshadowPosY = ballA.position.y
        if (abs(ballA.collisionGridCell!!.first - ballB.collisionGridCell!!.first) > 1) {
            ballAshadowPosX = wrappedCoordinate(
                ballB.position.x,
                ballA.position.x,
                gameState.gameBoundary.width()
            )
        }
        if (abs(ballA.collisionGridCell!!.second - ballB.collisionGridCell!!.second) > 1) {
            ballAshadowPosY = wrappedCoordinate(
                ballB.position.y,
                ballA.position.y,
                gameState.gameBoundary.height()
            )
        }
        ballAshadowPos = Vector(ballAshadowPosX,ballAshadowPosY)
    }

    private fun wrappedCoordinate(base: Double, wrapping: Double, wrapOffset: Double): Double {
        var wrappedCoordinate = wrapping
        var minDelta = abs(wrapping - base)
        if (abs((wrapping - wrapOffset) - base) < minDelta) {
            minDelta = abs((wrapping - wrapOffset) - base)
            wrappedCoordinate = wrapping - wrapOffset
        }
        if (abs((wrapping + wrapOffset) - base) < minDelta) {
            wrappedCoordinate = wrapping + wrapOffset
        }
        return wrappedCoordinate
    }

    private fun vN(): VectorN {
        return VectorN(ballB.velocity.x, ballA.velocity.x, ballB.velocity.y, ballA.velocity.y)
    }

    private fun deltaR(): VectorN {
        val rBA = ballB.position.minus(ballAshadowPos)
        return VectorN(rBA.x, -rBA.x, rBA.y, -rBA.y).times(1.0 / rBA.mag())
    }

    private fun coeffRest(): Double {
        return min(ballA.cOr, ballB.cOr)
    }
}