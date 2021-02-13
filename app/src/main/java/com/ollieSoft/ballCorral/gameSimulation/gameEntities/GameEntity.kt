package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.GameState

open class GameEntity {

    //Signals clients other than primary list to drop references
    //to avoid memory leaks and null dereferences
    var dead = true

    open fun addSelfToGameState(gameState: GameState) {
        gameState.gameEntityList.addEntity(this)
        dead = false
    }

    open fun markSelfForRemoval(gameState: GameState) {
        gameState.gameEntityList.markForRemoval(this)
        dead = true
    }

}