package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.CollisionGrid
import com.ollieSoft.ballCorral.gameSimulation.GameState

abstract class CollidableEntity: GameEntity() {

    open fun collided(other:CollidableEntity): Boolean {return false}
    open fun handleCollision(other: CollidableEntity, gameState: GameState) {}
    abstract fun markCollisionGrid(collisionGrid: CollisionGrid): Unit
    abstract fun unMarkCollisionGrid(collisionGrid: CollisionGrid)

    override fun addSelfToGameState(gameState: GameState) {
        super.addSelfToGameState(gameState)
        if (this !is MobileEntity) {
            markCollisionGrid(gameState.collisionGrid)
        }
    }

    open fun getPotentialColliders(collisionGrid: CollisionGrid): List<CollidableEntity> {
        return emptyList<CollidableEntity>()
    }

    override fun markSelfForRemoval(gameState: GameState) {
        super.markSelfForRemoval(gameState)
        if (this !is MobileEntity) {
            unMarkCollisionGrid(gameState.collisionGrid)
        }
    }

}