package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.CollisionGrid
import com.ollieSoft.ballCorral.gameSimulation.GameBoundary

interface MobileEntity {

    fun getPotentialColliders(collisionGrid: CollisionGrid): List<CollidableEntity>
    fun travel(dt: Double, gameBoundary: GameBoundary)

}