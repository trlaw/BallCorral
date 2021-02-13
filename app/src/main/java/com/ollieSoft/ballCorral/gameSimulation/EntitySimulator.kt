package com.ollieSoft.ballCorral.gameSimulation

import android.content.res.Resources
import com.ollieSoft.ballCorral.gameSimulation.constraintSolver.ConstraintSolver
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.CollidableEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.MobileEntity
import com.ollieSoft.ballCorral.paintableShapes.PaintableShapeList
import com.ollieSoft.ballCorral.utility.Vector

class EntitySimulator() {

    var entitySimulationState = EntitySimulationState.NONE
    private lateinit var gameState: GameState
    private var constraintSolver = ConstraintSolver()
    private var restartFlag: Boolean = false

    fun endGame() {
        entitySimulationState = EntitySimulationState.ENDED
    }

    fun initialize(screenDims: Vector? = null, resObj: Resources) {
        entitySimulationState = EntitySimulationState.NONE
        if (validScreenDimensions(screenDims)) {
            gameState = GameState(resObj, GameBoundary(screenDims!!))
            entitySimulationState = EntitySimulationState.INITIALIZED
        }
    }

    private fun restartIfRequested() {
        if (restartFlag) {
            restartFlag = false
            gameState.reset()
        }
    }

    private fun scheduleRestart() {
        restartFlag = true
    }

    fun updateState(dt: Double) {

        if (entitySimulationState == EntitySimulationState.RUNNING) {

            //Add ball to game if spawn conditions satisfied
            BallSpawner.tryAddBall(gameState)

            //Advance mobile object positions
            updateMobileEntityPositions(dt)

            //Populate collision grid with mobile objects
            markCollisionGridWithMobileEntities(gameState)

            //Adjust mobile objects according to collisions
            processMobileEntityCollisions()

            //Remove any entities destroyed in collision events
            gameState.gameEntityList.removeMarkedEntities()

            //Update velocities with constraint solver
            constraintSolver.updateVelocities(gameState,dt)

            //Remove mobile entities from collision grid since position may change on next step
            unMarkMobileEntitiesFromGrid(gameState)

            gameState.gameTime += dt
        }
        restartIfRequested()
    }


    //State Update Helper Methods
    private fun markCollisionGridWithMobileEntities(gameState: GameState) {
        val refList = gameState.gameEntityList
        val refGrid = gameState.collisionGrid
        var i = 0
        while (i < refList.size) {
            if ((refList[i] is CollidableEntity) && (refList[i] is MobileEntity)) {
                (refList[i] as CollidableEntity).markCollisionGrid(refGrid)
            }
            i++
        }
    }

    private fun processMobileEntityCollisions() {
        gameState.gameEntityList.forEach { it ->
            if (it is BallEntity) {
                it.getPotentialColliders(gameState.collisionGrid).forEach { other ->
                    if (it.collided(other)) {
                        it.handleCollision(other,gameState)
                    }
                }
            }
        }
    }

    private fun unMarkMobileEntitiesFromGrid(gameState: GameState) {
        for (gameEntity in gameState.gameEntityList) {
            if ((gameEntity is CollidableEntity) && (gameEntity is MobileEntity)) {
                gameEntity.unMarkCollisionGrid(gameState.collisionGrid)
            }
        }
    }

    private fun updateMobileEntityPositions(dt: Double) {
        gameState.gameEntityList.forEach { it ->
            if (it is MobileEntity) {
                it.travel(dt,gameState.gameBoundary)
            }
        }
    }

    //Entity Adders
    fun addPlayerBarrier(barrier: Pair<Vector, Vector>) {
        if (entitySimulationState == EntitySimulationState.RUNNING) {
            gameState.tryAddBarrier(barrier)
        }
    }

    private fun validScreenDimensions(screenDims: Vector?): Boolean {
        return (screenDims != null) && (screenDims.x != 0.0) && (screenDims.y != 0.0)
    }

    fun getPaintableObjects(): PaintableShapeList? {
        return gameState.getPaintableObjects()
    }

}

enum class EntitySimulationState {
    NONE, INITIALIZED, RUNNING, PAUSED, ENDED
}
