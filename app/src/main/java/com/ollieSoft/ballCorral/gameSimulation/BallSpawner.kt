package com.ollieSoft.ballCorral.gameSimulation

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.CollidableEntity
import com.ollieSoft.ballCorral.utility.Vector
import kotlin.random.Random

object BallSpawner {

    private const val ballInitVmin = R.string.BALL_INIT_V_MIN.toDouble()
    private const val ballInitVmax = R.string.BALL_INIT_V_MAX.toDouble()

    private fun ballCount(gameState: GameState): Int {
        return gameState.gameEntityList.count { it is BallEntity }
    }

    private fun setSpawnState(newBall: BallEntity, gameState: GameState) {
        newBall.apply {
            position = getBallSpawnPosition(gameState.gameBoundary)
            velocity = getBallSpawnVelocity()
        }
    }

    private fun getBallSpawnPosition(gameBoundary: GameBoundary, ballEntity: BallEntity): Vector {
        return gameBoundary.randomPositionInside()
    }

    private fun getBallSpawnVelocity(): Vector {
        return Vector.randomUnit()
            .times(ballInitVmin + Random.nextDouble() * (ballInitVmax - ballInitVmin))
    }

    fun tryAddBall(gameState: GameState) {

        val numberOfBalls = ballCount(gameState)

        //Add ball only if enough time elapsed
        if (gameState.gameTime < (numberOfBalls * R.string.BALL_ADD_TIME.toDouble())) {
            return
        }

        //Don't add if already at maximum allowed ball count
        if (numberOfBalls >= R.integer.BALL_LIMIT) {
            return
        }

        val newBall = gameState.entityFactoryManager.ballFactory.create()
        setSpawnState(newBall, gameState)

        //Add ball only if it will not collide with an existing entity
        if (entityList.none { gameEntity ->
                ((gameEntity is CollidableEntity) && (gameEntity.collided(newBall)))
            }) {
            addEntity(newBall)
            incrementScore()
        }
    }

}