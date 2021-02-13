package com.ollieSoft.ballCorral.gameSimulation

import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BarrierEntity

class PlayerBarrierList : Iterable<BarrierEntity> {

    private val backingList = mutableListOf<BarrierEntity>()
    private var totalBarrierLength = 0.0

    fun add(barrierEntity: BarrierEntity,gameState: GameState) {
        backingList.add(barrierEntity)
        totalBarrierLength += barrierEntity.length
        val maxBarrierLength = GameDifficultyMap.playerBarrierTotalLength(gameState.score)
        while (totalBarrierLength > maxBarrierLength) {
            val oldestBarrier = backingList.removeAt(0)
            totalBarrierLength -= oldestBarrier.length
            oldestBarrier.markSelfForRemoval(gameState)
        }
        gameState.gameEntityList.removeMarkedEntities()
    }

    override fun iterator(): Iterator<BarrierEntity> {
        return backingList.iterator()
    }

    fun reset() {
        backingList.clear()
        totalBarrierLength = 0.0
    }

    val size: Int
        get() {
            return backingList.size
        }
}