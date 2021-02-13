package com.ollieSoft.ballCorral.gameSimulation

import com.ollieSoft.ballCorral.gameSimulation.gameEntities.GameEntity

open class GameEntityList: ArrayList<GameEntity>() {

    private val removalBuffer = mutableSetOf<GameEntity>()

    fun addEntity(gameEntity: GameEntity) {
        this.add(gameEntity)
    }

    fun markForRemoval(gameEntity: GameEntity) {
        removalBuffer.add(gameEntity)
    }

    fun removeMarkedEntities() {
        this.removeAll(removalBuffer)
        removalBuffer.clear()
    }

}