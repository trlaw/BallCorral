package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.CollisionGrid
import com.ollieSoft.ballCorral.paintableShapes.PaintableCircle
import com.ollieSoft.ballCorral.paintableShapes.PaintableShape
import com.ollieSoft.ballCorral.utility.Vector

class BallEntity() : CollidableEntity(), PaintableEntity, MobileEntity {

    var checkedEntities = mutableSetOf<CollidableEntity>()
    var collisionGridCell: Pair<Int, Int>? = null
    var colorIndex: Int = 0
    var cOr: Double = R.string.DEFAULT_COR.toDouble()
    private val maxSpeed = R.string.MAX_BALL_SPEED.toDouble()
    var position = Vector.zero()
    var radius: Double = R.string.BALL_RADIUS.toDouble()
    var velocity = Vector.zero()

    override fun getPaintableShape(): PaintableShape {
        return PaintableCircle(position, radius, colorIndex)
    }

    override fun getPotentialColliders(collisionGrid: CollisionGrid): List<CollidableEntity> {
        val potentialColliders = mutableListOf<CollidableEntity>()
        if (collisionGridCell == null) {
            return potentialColliders
        }
        collisionGrid.getCollisionKeys(collisionGridCell!!)
            .forEach { key ->
                collisionGrid.getCellEntities(key)?.let { collidableList ->
                    collidableList.forEach {
                        if ((!potentialColliders.contains(it)) && (it !== this)) {
                            potentialColliders.add(it)
                        }
                    }
                }
            }
        return potentialColliders
    }

    override fun markCollisionGrid(collisionGrid: CollisionGrid) {
        val cell = collisionGrid.getKeyForPosition(position)
        collisionGrid.markGridCell(cell, this)
        collisionGridCell = cell
    }

    override fun travel(dt: Double): Unit {
        if (velocity.mag() > maxSpeed) {
            velocity = velocity.times(maxSpeed / velocity.mag())
        }
        position = position.plus(velocity.times(dt))
    }

    override fun unMarkCollisionGrid(collisionGrid: CollisionGrid) {
        collisionGrid.unMarkGridCell(collisionGridCell, this)
        collisionGridCell = null
    }

}