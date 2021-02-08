package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.CollisionGrid

interface MobileEntity {

    fun travel(dt: Double)
    fun getPotentialColliders(collisionGrid: CollisionGrid): List<CollidableEntity>

}