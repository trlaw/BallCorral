package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.CollisionGrid
import com.ollieSoft.ballCorral.gameSimulation.GameBoundary

interface MobileEntity {

    fun travel(dt: Double, gameBoundary: GameBoundary)

}