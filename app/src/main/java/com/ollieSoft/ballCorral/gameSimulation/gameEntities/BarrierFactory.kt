package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.utility.Vector

abstract class BarrierFactory {
    abstract fun create(start:Vector, end: Vector): BarrierEntity
}