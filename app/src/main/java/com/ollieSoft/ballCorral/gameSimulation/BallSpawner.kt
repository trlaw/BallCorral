package com.ollieSoft.ballCorral.gameSimulation

import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.CollidableEntity
import com.ollieSoft.ballCorral.utility.Vector
import kotlin.random.Random

object BallSpawner {

    private fun ballCount(gameState: GameState): Int {
        return gameState.gameEntityList.count { it is BallEntity }
    }

    private fun setSpawnState(newBall: BallEntity, gameState: GameState) {
        newBall.apply {
            position = getBallSpawnPosition(gameState.gameBoundary)
            velocity = getBallSpawnVelocity(gameState.score)
        }
    }

    private fun getBallSpawnPosition(gameBoundary: GameBoundary): Vector {
        return gameBoundary.randomPositionInside()
    }

    private fun getBallSpawnVelocity(score: Long): Vector {
        return Vector.randomUnit()
            .times(
                GameDifficultyMap.initVmin(score) + (Random.nextDouble()
                        * (GameDifficultyMap.initVmax(score) - GameDifficultyMap.initVmin(score)))
            )
    }

    fun tryAddBall(gameState: GameState) {

        val numberOfBalls = ballCount(gameState)

        //Add ball only if enough time elapsed
        if (numberOfBalls >= GameDifficultyMap.maxBalls(gameState.score)) {
            return
        }

        val newBall = gameState.entityFactoryManager.ballFactory.create()
        setSpawnState(newBall, gameState)

        //Add ball only if it will not collide with an existing entity
        if (gameState.gameEntityList.none { gameEntity ->
                ((gameEntity is CollidableEntity) && (gameEntity.collided(newBall)))
            }) {
            newBall.addSelfToGameState(gameState)
        }
    }

}