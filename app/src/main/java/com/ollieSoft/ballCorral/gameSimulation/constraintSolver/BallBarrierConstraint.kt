package com.ollieSoft.ballCorral.gameSimulation.constraintSolver

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.GameState
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BarrierEntity
import com.ollieSoft.ballCorral.utility.Vector
import com.ollieSoft.ballCorral.utility.inClosedRectangle
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.pow

class BallBarrierConstraint(
    private val ball: BallEntity,
    private val barrier: BarrierEntity,
    idleLife: Double
) :
    AbstractConstraint(idleLife) {

    private val ballBarrierBeta = R.string.BALL_BARRIER_BETA.toDouble()
    private val ballBarrierSlopFactor = R.string.BALL_BARRIER_SLOP_FACTOR.toDouble()
    private val gamma: Double by lazy { sBA.y / sBA.mag() }
    private var impulse = Vector.zero()
    private val lowerDotRange: Double by lazy {
        (-1.0) * (barrier.start!!.dot(barrier.start!!) - sAdotSb)
    }
    private val minEndDist = ball.radius + barrier.width / 2.0
    private val sAdotSb: Double by lazy { barrier.start!!.dot(barrier.end!!) }
    private val sBA: Vector by lazy { barrier.end!!.minus(barrier.start!!) }
    private val sBAnormal: Vector by lazy {
        Vector(
            gamma,
            if (sBA.y == 0.0) 1.0 else (-1.0) * gamma * (sBA.x / sBA.y)
        )
    }
    private val upperDotRange: Double by lazy {
        barrier.end!!.dot(barrier.end!!) - sAdotSb
    }
    private var ballShadowPos = Vector.zero()
    private var vInit: Vector = Vector.zero()

    override fun applyCorrectiveImpulses() {
        ball.velocity = ball.velocity.plus(impulse)
        resetLifeTimeCounter()
    }

    private fun ballInsideBarrierProjection(): Boolean {
        return posInsideBarrierProjection(ballShadowPos)
    }

    private fun posInsideBarrierProjection(position: Vector): Boolean {
        val locationDot = position.dot(sBA)
        return ((locationDot >= lowerDotRange) && (locationDot <= upperDotRange))
    }

    private fun barrierEdge(side: BarrierEdgeConstraint): Vector {
        return if (side == BarrierEdgeConstraint.START) barrier.start else barrier.end
    }

    private fun closestEdge(): BarrierEdgeConstraint {
        return if (evalCedge(BarrierEdgeConstraint.START) <= evalCedge(BarrierEdgeConstraint.END))
            BarrierEdgeConstraint.START else BarrierEdgeConstraint.END
    }

    private fun edgeJacobian(side: BarrierEdgeConstraint): Vector {
        return ballShadowPos.minus(barrierEdge(side)).unitScaled()
    }

    override fun evalC(): Double {
        return pointBarrierDistance(ballShadowPos) - (barrier.width / 2 + ball.radius)
    }

    private fun pointBarrierDistance(position: Vector): Double {
        return abs(position.minus(barrier.start!!).dot(sBAnormal))
    }

    override fun evalCdot(): Double {
        return jacobian().dot(ball.velocity)
    }

    private fun evalCdotEdge(side: BarrierEdgeConstraint): Double {
        return edgeJacobian(side).dot(ball.velocity)
    }

    private fun evalCedge(side: BarrierEdgeConstraint): Double {
        return ballShadowPos.minus(barrierEdge(side)).mag() - minEndDist
    }

    override fun evalImpulses(dt: Double) {
        val thisC = evalC()
        val jcbn = if (ballInsideBarrierProjection()) jacobian() else edgeJacobian(closestEdge())
        val lmda = lambda(jcbn, thisC, dt)
        impulse = jcbn.times(lmda)
    }

    private fun jacobian(): Vector {
        return sBAnormal.times(
            if (ballShadowPos.minus(barrier.start!!).dot(sBAnormal) < 0.0) -1.0 else 1.0
        )
    }

    private fun lambda(jcbn: Vector, valueC: Double, dt: Double): Double {
        return (-1.0) *
                ((jcbn.dot(ball.velocity.plus(vInit.times(ball.cOr))) +
                        (ballBarrierBeta / dt) *
                        (if (valueC < (-1.0) * ball.radius * ballBarrierSlopFactor)
                            (valueC + ball.radius * ballBarrierSlopFactor) else 0.0)) / (jcbn.mag()
                    .pow(2)))
    }

    override fun resetInitialQuantities(gameState: GameState) {
        computeBallShadowPos(gameState)
        vInit = ball.velocity
    }

    private fun computeBallShadowPos(gameState: GameState) {
        val gmBoundary = gameState.gameBoundary
        ballShadowPos = ball.position
        if (inClosedRectangle(
                gmBoundary.lowerBounds.plus(ball.vectorOffset),
                gmBoundary.upperBounds.minus(ball.vectorOffset),
                ball.position
            )
        ) {
            return
        } else {
            //Check possible wraps to closer equivalent positions
            val ballGridCell = ball.collisionGridCell!!
            val gmWidth = gmBoundary.width()
            val gmHeight = gmBoundary.height()
            var minDistance = minBarrierDistance(ballShadowPos)
            var wrapX: Int = 0
            var wrapY: Int = 0

            if (ballGridCell.first == 0) {
                wrapX = -1
                minDistance = checkWrappedPosition(Vector(gmWidth, 0.0), minDistance)
            } else if (ballGridCell.first == (gameState.collisionGrid.numWidthCells - 1)) {
                wrapX = 1
                minDistance = checkWrappedPosition(Vector((-1.0) * gmWidth, 0.0), minDistance)
            }
            if (ballGridCell.second == 0) {
                wrapY = -1
                minDistance = checkWrappedPosition(Vector(0.0, gmHeight), minDistance)
            } else if (ballGridCell.second == (gameState.collisionGrid.numHeightCells - 1)) {
                wrapY = 1
                minDistance = checkWrappedPosition(Vector(0.0, (-1.0) * gmHeight), minDistance)
            }
            if ((abs(wrapX) + abs(wrapY)) == 2) {
                checkWrappedPosition(
                    Vector(
                        (-wrapX).toDouble() * gmWidth,
                        (-wrapY).toDouble() * gmHeight
                    ), minDistance
                )
            }
        }
    }

    //Checks if alternative position is closer to barrier.  If it is, reassigns ballShadowPos.
    //Returns minimum distance from barrier among the original and new positions
    private fun checkWrappedPosition(offset: Vector, currentMin: Double): Double {
        val testVector = ballShadowPos.plus(offset)
        val minTestDistance = minBarrierDistance(testVector)
        if (minTestDistance < currentMin) {
            ballShadowPos = testVector
            return minTestDistance
        }
        return currentMin
    }

    private fun minBarrierDistance(position: Vector): Double {
        var minDistance: Double = POSITIVE_INFINITY
        if (posInsideBarrierProjection(position)) {
            minDistance = pointBarrierDistance(position)
        }
        return min(
            minDistance,
            min(position.minus(barrier.start).mag(), position.minus(barrier.end).mag())
        )
    }

    override fun satisfied(): Boolean {
        val locationDot = ballShadowPos.dot(sBA)
        //Check if ball center inside normal projection of barrier
        if (!(ballInsideBarrierProjection())) {
            return satisfiedEdge(closestEdge())
        }
        return !((evalC() < 0.0) && (evalCdot() < 0.0))
    }

    private fun satisfiedEdge(closestEdge: BarrierEdgeConstraint): Boolean {
        return ((evalCedge(closestEdge) >= 0) || (evalCdotEdge(closestEdge) > 0))
    }

}

enum class BarrierEdgeConstraint {
    START, END
}