package com.ollieSoft.ballCorral.gameSimulation

import com.ollieSoft.ballCorral.R

class PlayerBarrierList : Iterable<BarrierEntity> {

    private val backingList = mutableListOf<BarrierEntity>()
    private val maxBarrierLength = R.string.MAX_BARRIER_LENGTH.toDouble()
    private var totalBarrierLength = 0.0

    override fun iterator(): Iterator<BarrierEntity> {
        return backingList.iterator()
    }

    val size: Int
        get() {
            return backingList.size
        }
}