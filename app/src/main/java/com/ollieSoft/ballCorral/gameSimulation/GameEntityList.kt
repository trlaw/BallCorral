package com.ollieSoft.ballCorral.gameSimulation

import com.ollieSoft.ballCorral.gameSimulation.gameEntities.GameEntity

open class GameEntityList: Iterable<GameEntity> {
    private val backingList = mutableSetOf<GameEntity>()
    private val removalBuffer = mutableSetOf<GameEntity>()

    private val aList = mutableListOf<GameEntity>()

    val size: Int
    get() {
        return backingList.size
    }

    fun addEntity(gameEntity: GameEntity) {
        backingList.add(gameEntity)
    }

    fun clear() {
        backingList.clear()
    }

    override fun iterator(): Iterator<GameEntity> {
        return backingList.iterator()
    }

    fun markForRemoval(gameEntity: GameEntity) {
        removalBuffer.add(gameEntity)
    }

    fun removeMarkedEntities() {
        backingList.removeAll(removalBuffer)
        removalBuffer.clear()
    }

}