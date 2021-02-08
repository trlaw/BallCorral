package com.ollieSoft.ballCorral.gameSimulation

import android.content.res.Resources
import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.*
import com.ollieSoft.ballCorral.paintableShapes.PaintableShapeList
import com.ollieSoft.ballCorral.utility.Vector

class EntitySimulator() {

    private lateinit var gameState: GameState
    var entitySimulationState = EntitySimulationState.NONE
    private var restartFlag: Boolean = false

    fun endGame() {
        entitySimulationState = EntitySimulationState.ENDED
    }

    fun initialize(screenDims: Vector? = null, resObj: Resources) {
        entitySimulationState = EntitySimulationState.NONE
        if (!validScreenDimensions(screenDims)) {
            return
        }
        gameState = GameState(resObj,GameBoundary(screenDims!!))
        entitySimulationState = EntitySimulationState.INITIALIZED
    }


    private fun restartIfRequested() {
        if (restartFlag) {
            restartFlag = false
            initialize()
        }
    }

    private fun scheduleRestart() {
        restartFlag = true
    }

    fun updateState(dt: Double) {

        if (entitySimulationState == EntitySimulationState.RUNNING) {

            //Add ball to game if spawn conditions satisfied
            tryAddBall()

            //Advance mobile object positions
            updateMobileEntityPositions(dt)

            //Populate collision grid with mobile objects
            markCollisionGridWithMobileEntities()

            //Adjust mobile objects according to collisions
            processMobileEntityCollisions()

            //Remove mobile entities from collision grid since position may change on next step
            unMarkMobileEntitiesFromGrid()

            removeMarkedEntities() //Remove entity list items marked for removal

            simTime += dt
        }
        restartIfRequested()
    }


    //State Update Helper Methods
    private fun markCollisionGridWithMobileEntities() {
        var i = 0
        while (i < entityList.size) {
            if ((entityList[i] is CollidableEntity) && (entityList[i] is MobileEntity)) {
                (entityList[i] as CollidableEntity).markCollisionGrid(collisionGrid)
            }
            i++
        }
    }

    private fun processMobileEntityCollisions() {
        entityList.forEach { it ->
            if (it is MobileEntity) {
                it.reactToCollisions(collisionGrid)
            }
        }
    }

    private fun unMarkMobileEntitiesFromGrid() {
        for (gameEntity in entityList) {
            if ((gameEntity is CollidableEntity) && (gameEntity is MobileEntity)) {
                gameEntity.unMarkCollisionGrid(collisionGrid)
            }
        }
    }

    private fun updateMobileEntityPositions(dt: Double) {
        entityList.forEach { it ->
            if (it is MobileEntity) {
                it.travel(dt)
            }
        }
    }

    //Entity Adders
    fun addPlayerBarrier(barrier: Pair<Vector, Vector>) {
        if (entitySimulationState == EntitySimulationState.RUNNING) {
            tryAddBarrier(barrier)
        }
    }

    private fun tryAddBarrier(barrier: Pair<Vector, Vector>) {
        val newBarrier = playerBarrierFactory.create().apply {
            start = barrier.first
            end = barrier.second
        }
        //Disallow barriers in contact with ball
        if (entityList.none { gameEntity ->
                ((gameEntity is BallEntity) && (gameEntity.collided(newBarrier)))
            }) {
            addEntity(newBarrier)
            playerBarrierList.add(newBarrier)
            if (playerBarrierList.size > MAX_BARRIERS) {
                markForRemoval(playerBarrierList.removeAt(0))
            }
        }
    }

    private fun tryAddBall() {

        //Add ball only if enough time elapsed
        if (simTime < (getScore() * R.string.BALL_ADD_TIME.toDouble())) {
            return
        }

        //Don't add if already at maximum allowed ball count
        if ((entityList.count { it -> it is BallEntity }) >= R.integer.BALL_LIMIT) {
            return
        }

        val newBall = gameBallFactory.create()
        gameBoundary!!.setSpawnState(newBall)

        //Add ball only if it will not collide with an existing entity
        if (entityList.none { gameEntity ->
                ((gameEntity is CollidableEntity) && (gameEntity.collided(newBall)))
            }) {
            addEntity(newBall)
            incrementScore()
        }
    }

     private fun validScreenDimensions(screenDims: Vector?): Boolean {
        return (screenDims != null) && (screenDims.x != 0.0) && (screenDims.y != 0.0)
    }

}

enum class EntitySimulationState {
    NONE, INITIALIZED, RUNNING, PAUSED, ENDED
}
