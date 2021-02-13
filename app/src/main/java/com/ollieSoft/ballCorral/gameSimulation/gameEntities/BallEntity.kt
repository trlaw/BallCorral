package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.CollisionGrid
import com.ollieSoft.ballCorral.gameSimulation.GameBoundary
import com.ollieSoft.ballCorral.gameSimulation.GameState
import com.ollieSoft.ballCorral.paintableShapes.PaintableCircle
import com.ollieSoft.ballCorral.paintableShapes.PaintableCircleSet
import com.ollieSoft.ballCorral.paintableShapes.PaintableShape
import com.ollieSoft.ballCorral.utility.Vector
import com.ollieSoft.ballCorral.utility.inClosedRectangle

class BallEntity() : CollidableEntity(), PaintableEntity, MobileEntity {

    var checkedEntities = mutableSetOf<CollidableEntity>()
    var collisionGridCell: Pair<Int, Int>? = null
    var colorIndex: Int = 0
    var cOr: Double = R.string.DEFAULT_COR.toDouble()
    private val maxSpeed = R.string.MAX_BALL_SPEED.toDouble()
    var position = Vector.zero()
    var radius: Double = R.string.BALL_RADIUS.toDouble()
    var vectorOffset = Vector(this.radius, this.radius)
    var velocity = Vector.zero()

    override fun getPaintableShape(gameState: GameState): PaintableShape {
        return if (inClosedRectangle(
                Vector.zero(), gameState.gameBoundary.upperBounds.minus(vectorOffset), position
            )
        ) {
            PaintableCircle(position, radius, colorIndex)
        } else {
            getPaintableWrappedCircle(gameState)
        }
    }

    private fun getPaintableWrappedCircle(gameState: GameState): PaintableCircleSet {
        val outList = PaintableCircleSet()
        val viewableLower = vectorOffset.times(-1.0)
        val viewableUpper = gameState.gameBoundary.upperBounds.plus(vectorOffset)
        for (i in -1..1) {
            for (j in -1..1) {
                if ((i != 0) || (j != 0)) {
                    val wrapPosition = Vector(
                        position.x + i * gameState.gameBoundary.width(),
                        position.y + j * gameState.gameBoundary.height()
                    )
                    if (inClosedRectangle(viewableLower, viewableUpper, wrapPosition)) {
                        outList.add(PaintableCircle(wrapPosition, radius, colorIndex))
                    }
                }
            }
        }
        return outList
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

    override fun collided(other: CollidableEntity): Boolean {
        if (other is BallEntity)  {
            if (((!this.dead) && (!other.dead)) && (this.position.minus(other.position)
                    .mag() < (this.radius + other.radius))
            ) {
                return true
            }
        }
        return false
    }

    override fun handleCollision(other: CollidableEntity, gameState: GameState) {
        if  (((!this.dead) && (!other.dead)) && (other is BallEntity)) {
            gameState.score = gameState.score + (if (this.colorIndex == other.colorIndex) 1 else -2)
            PairedBallEntity(this, other).addSelfToGameState(gameState)
            this.markSelfForRemoval(gameState)
            other.markSelfForRemoval(gameState)
        }
    }

    override fun markCollisionGrid(collisionGrid: CollisionGrid) {
        val cell = collisionGrid.getKeyForPosition(position)
        collisionGrid.markGridCell(cell, this)
        collisionGridCell = cell
    }

    override fun travel(dt: Double, gameBoundary: GameBoundary) {
        if (velocity.mag() > maxSpeed) {
            velocity = velocity.times(maxSpeed / velocity.mag())
        }

        //Wrap-around if reach end of game boundary
        position = position.plus(velocity.times(dt)).plus(gameBoundary.upperBounds)
            .moduloEach(gameBoundary.upperBounds)

    }

    override fun unMarkCollisionGrid(collisionGrid: CollisionGrid) {
        collisionGrid.unMarkGridCell(collisionGridCell, this)
        collisionGridCell = null
    }

}