package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.utility.Vector

class PlayerBarrierFactory : BarrierFactory() {

    override fun create(start: Vector, end: Vector): BarrierEntity {
        return BarrierEntity(start, end, R.string.PLAYER_BARRIER_WIDTH.toDouble())
    }

}
