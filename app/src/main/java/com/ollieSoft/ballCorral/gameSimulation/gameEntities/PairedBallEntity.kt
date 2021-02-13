package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.GameBoundary
import com.ollieSoft.ballCorral.gameSimulation.GameState
import com.ollieSoft.ballCorral.paintableShapes.PaintableCircle
import com.ollieSoft.ballCorral.paintableShapes.PaintableCircleSet
import com.ollieSoft.ballCorral.paintableShapes.PaintableShape
import com.ollieSoft.ballCorral.utility.Vector
import kotlin.math.*

class PairedBallEntity(private val ballA: BallEntity, private val ballB: BallEntity) : GameEntity(),
    MobileEntity, PaintableEntity {

    private val fadeRate = R.string.PAIRED_BALL_FADERATE.toDouble()
    private val maxOmega = R.string.MAX_PAIRED_BALL_ANGULAR_V.toDouble()
    private val maxVelocity = R.string.MAX_PAIRED_BALL_SPEED.toDouble()

    var opacity = 256.0
    var position = ballA.position.plus(ballB.position).times(0.5)
    val rCMa = ballA.position.minus(this.position).mag()
    val rCMb = ballB.position.minus(this.position).mag()
    var angle = ballA.position.minus(ballB.position).angle
    private val unClampedVelocity = ballA.velocity.plus(ballB.velocity).times(0.5)
    private val velocity =
        unClampedVelocity.times(
            if (unClampedVelocity.mag() > maxVelocity)
                (maxVelocity / unClampedVelocity.mag()) else 1.0
        )
    private val unClampedOmega =
        (1.0 / (ballA.radius + ballB.radius)) * (ballA.position.minus(this.position)
            .cross(ballA.velocity)
                + ballB.position.minus(this.position).cross(ballB.velocity))
    private val omega =
        if (abs(unClampedOmega) > maxOmega) (maxOmega * sign(unClampedOmega)) else unClampedOmega

    override fun travel(dt: Double, gameBoundary: GameBoundary) {
        opacity -= fadeRate * dt
        position = position.plus(velocity.times(dt))
        angle = (angle + dt * omega).rem(2 * kotlin.math.PI)
    }

    override fun getPaintableShape(gameState: GameState): PaintableShape {
        if (opacity < 0.0) {
            opacity = 0.0
            this.markSelfForRemoval(gameState)
        }
        val opacityRounded = opacity.roundToInt()
        val outShape = PaintableCircleSet()
        outShape.add(
            PaintableCircle(
                position.plus(Vector(cos(angle), sin(angle)).times(rCMa)),
                ballA.radius, ballA.colorIndex, opacityRounded
            )
        )
        outShape.add(
            PaintableCircle(
                position.plus(Vector((-1.0)*cos(angle),(-1.0)*sin(angle)).times(rCMb)),
                ballB.radius, ballB.colorIndex, opacityRounded
            )
        )
        return outShape
    }
}