package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.BarrierEntity

abstract class BarrierFactory {
    abstract fun create(): BarrierEntity
}