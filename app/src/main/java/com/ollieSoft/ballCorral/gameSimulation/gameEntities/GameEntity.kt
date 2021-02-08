package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.GameState

open class GameEntity {

    open fun addSelfToGameState(gameState: GameState) {
        gameState.gameEntityList.addEntity(this)
    }

    open fun markSelfForRemoval(gameState: GameState) {
        gameState.gameEntityList.markForRemoval(this)
    }

}