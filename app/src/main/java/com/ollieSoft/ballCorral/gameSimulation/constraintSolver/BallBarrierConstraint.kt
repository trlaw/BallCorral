package com.ollieSoft.ballCorral.gameSimulation.constraintSolver

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.BarrierEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.utility.Vector
import kotlin.math.abs
import kotlin.math.pow

class BallBarrierConstraint(
    private val ball: BallEntity,
    private val barrier: BarrierEntity,
    idleLife: Double
) :
    AbstractConstraint(idleLife) {

    private val ballBarrierBeta = R.string.BALL_BARRIER_BETA.toDouble()
    private val ballBarrierSlopFactor = R.string.BALL_BARRIER_SLOP_FACTOR.toDouble()

    private var vInit: Vector = Vector.zero()
    var impulse = Vector.zero()

    override fun satisfied(): Boolean {
        val locationDot = ball.position.dot(sBA)
        //Check if ball center inside normal projection of barrier
        if ((locationDot < lowerDotRange) || (locationDot > upperDotRange)) {
            return true
        }
        return !((evalC() < 0.0) && (evalCdot() < 0.0))
    }

    override fun evalC(): Double {
        return abs(
            ball.position.minus(barrier.start!!).dot(sBAnormal)
        ) - (barrier.width / 2 + ball.radius)
    }

    override fun evalCdot(): Double {
        return jacobian().dot(ball.velocity)
    }

    override fun evalImpulses(dt: Double) {
        val thisC = evalC()
        val lambda =
            (-1.0) *
                    ((jacobian().dot(ball.velocity.plus(vInit.times(ball.cOr))) +
                            (ballBarrierBeta / dt) *
                            (if (thisC < (-1.0) * ball.radius * ballBarrierSlopFactor)
                                (thisC + ball.radius * ballBarrierSlopFactor) else 0.0)) / (jacobian().mag()
                        .pow(2)))
        impulse = jacobian().times(lambda)
    }

    override fun resetInitialQuantities() {
        vInit = ball.velocity
    }

    override fun applyCorrectiveImpulses() {
        ball.velocity = ball.velocity.plus(impulse)
        resetLifeTimeCounter()
    }

    private val sBA: Vector by lazy { barrier.end!!.minus(barrier.start!!) }
    private val gamma: Double by lazy { sBA.y / sBA.mag() }
    private val sBAnormal: Vector by lazy {
        Vector(
            gamma,
            if (sBA.y == 0.0) 1.0 else (-1.0) * gamma * (sBA.x / sBA.y)
        )
    }
    private val sAdotSb: Double by lazy { barrier.start!!.dot(barrier.end!!) }
    private val lowerDotRange: Double by lazy {
        (-1.0) * (barrier.start!!.dot(barrier.start!!) - sAdotSb)
    }
    private val upperDotRange: Double by lazy {
        barrier.end!!.dot(barrier.end!!) - sAdotSb
    }

    private fun jacobian(): Vector {
        return sBAnormal.times(
            if (ball.position.minus(barrier.start!!).dot(sBAnormal) < 0.0) -1.0 else 1.0
        )
    }

}