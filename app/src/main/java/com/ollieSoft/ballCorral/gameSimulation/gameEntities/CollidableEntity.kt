package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.CollisionGrid
import com.ollieSoft.ballCorral.gameSimulation.GameState

abstract class CollidableEntity: GameEntity() {

    abstract fun markCollisionGrid(collisionGrid: CollisionGrid): Unit
    abstract fun unMarkCollisionGrid(collisionGrid: CollisionGrid)

    override fun addSelfToGameState(gameState: GameState) {
        super.addSelfToGameState(gameState)
        if (this !is MobileEntity) {
            markCollisionGrid(gameState.collisionGrid)
        }
    }

    override fun markSelfForRemoval(gameState: GameState) {
        super.markSelfForRemoval(gameState)
        if (this !is MobileEntity) {
            unMarkCollisionGrid(gameState.collisionGrid)
        }
    }

}