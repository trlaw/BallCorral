package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.CollisionGrid
import com.ollieSoft.ballCorral.gameSimulation.GameState
import com.ollieSoft.ballCorral.paintableShapes.PaintableLine
import com.ollieSoft.ballCorral.paintableShapes.PaintableShape
import com.ollieSoft.ballCorral.utility.Vector
import com.ollieSoft.ballCorral.utility.vectorMidpoint

enum class SegmentLocation {
    TOP, LEAF
}

open class BarrierEntity(val start: Vector, val end: Vector, val width: Double) :
    CollidableEntity(), PaintableEntity {

    var collisionCells: MutableSet<Pair<Int, Int>>? = null //Cache for collision cells touched
    val length = end.minus(start).mag()
    var unitNormal: Vector? = null

    private fun barrierUnitNormal(): Vector {
        if (unitNormal == null) {
            unitNormal = end.minus(start).unitNormal()
        }
        return unitNormal!!
    }

    private fun getEdges(): List<Pair<Vector, Vector>> {
        val vWidthOffset = barrierUnitNormal().times(width / (2f))
        val outList = mutableListOf<Pair<Vector, Vector>>()
        val vertices = arrayOf(
            start.plus(vWidthOffset),
            start.minus(vWidthOffset),
            end.minus(vWidthOffset),
            end.plus(vWidthOffset)
        )
        for (i in 0 until 4) {
            outList.add(Pair(vertices[i], vertices[(i + 1) % 4]))
        }
        return outList
    }

    override fun getPaintableShape(gameState: GameState): PaintableShape {
        return PaintableLine(start, end, width)
    }

    override fun markCollisionGrid(collisionGrid: CollisionGrid) {
        if (collisionCells == null) {
            collisionCells = mutableSetOf()
            //Check all lines to find overlap.  A cell which is completely enclosed by a barrier
            //will not contain any other entities against which collision could occur
            getEdges().forEach { it ->
                collisionCells!!.addAll(
                    markLineOnCollisionGrid(
                        collisionGrid,
                        it.first,
                        it.second,
                        SegmentLocation.TOP
                    )
                )
            }
        } else {

            collisionCells!!.forEach { it ->
                collisionGrid.markGridCell(it, this)

            }
        }
    }

    private fun markLineOnCollisionGrid(
        collisionGrid: CollisionGrid,
        lineStart: Vector,
        lineEnd: Vector,
        location: SegmentLocation
    ): Set<Pair<Int, Int>> {

        val cellSet = mutableSetOf<Pair<Int, Int>>()

        //Mark endpoints according to position on call tree
        if (location == SegmentLocation.TOP) {
            val topStartKey = collisionGrid.getKeyForPosition(lineStart)
            collisionGrid.markGridCell(topStartKey, this)
            cellSet.add(topStartKey)
            val topEndKey = collisionGrid.getKeyForPosition(lineEnd)
            collisionGrid.markGridCell(topEndKey, this)
            cellSet.add(topEndKey)
        }

        //Termination conditions
        val startKey = collisionGrid.getKeyForPosition(lineStart)
        val endKey = collisionGrid.getKeyForPosition(lineEnd)
        if (collisionGrid.cellsAreAdjacent(startKey, endKey) || collisionGrid.cellsAreDiagonal(
                startKey, endKey
            ) || (startKey == endKey)
        ) {
            return cellSet
        }

        //Mark midpoint and check interior segments
        val midPoint = vectorMidpoint(lineStart, lineEnd)
        val midKey = collisionGrid.getKeyForPosition(midPoint)
        collisionGrid.markGridCell(midKey, this)
        cellSet.add(midKey)
        cellSet.addAll(
            markLineOnCollisionGrid(
                collisionGrid,
                lineStart,
                midPoint,
                SegmentLocation.LEAF
            )
        )
        cellSet.addAll(
            markLineOnCollisionGrid(
                collisionGrid,
                midPoint,
                lineEnd,
                SegmentLocation.LEAF
            )
        )
        return cellSet
    }

    override fun unMarkCollisionGrid(collisionGrid: CollisionGrid) {
        if (collisionCells != null) {
            collisionCells!!.forEach { it -> collisionGrid.unMarkGridCell(it, this) }
        }
        collisionCells = null
    }

}
