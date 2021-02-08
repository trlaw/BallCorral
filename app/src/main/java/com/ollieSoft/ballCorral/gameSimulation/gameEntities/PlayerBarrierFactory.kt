package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.BarrierEntity

class PlayerBarrierFactory: BarrierFactory() {
    override fun create(): BarrierEntity {
        return BarrierEntity().apply {
            width = R.string.PLAYER_BARRIER_WIDTH.toDouble()
        }
    }
}