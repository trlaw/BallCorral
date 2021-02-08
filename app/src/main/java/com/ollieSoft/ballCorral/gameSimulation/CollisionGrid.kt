package com.ollieSoft.ballCorral.gameSimulation

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.CollidableEntity
import com.ollieSoft.ballCorral.utility.Vector
import com.ollieSoft.ballCorral.utility.invokeAllOrderedPairs
import kotlin.math.abs
import kotlin.math.floor

//Collection for pre-sorting objects coarsely by position for collision detection
//The neighboring cell references returned by getCollisionKeys will only cover all
//possible objects which can collide with the item in the given position if the maximum
//distance between any two points, on any of the objects, is less than or equal to
//maxEntitySize.  Objects may be larger than maxEntitySize by this measure, but in such case
//they will need to registered in the grid inside every cell over which they extend
class CollisionGrid(private val boundary: GameBoundary) {

    private val minCellSize = 2.0*R.string.BALL_RADIUS.toDouble()*R.string.GRID_SIZE.toDouble()
    private var numWidthCells: Int = 0
    private var numHeightCells: Int = 0
    private var cellWidth: Double = 0.0
    private var cellHeight: Double = 0.0

    private var gridMap: HashMap<Pair<Int, Int>, MutableSet<CollidableEntity>>

    init {
        numWidthCells = floor(boundary.width() / minCellSize).toInt()
        numHeightCells = floor(boundary.height() / minCellSize).toInt()
        cellWidth = boundary.width() / (numWidthCells.toDouble())
        cellHeight = boundary.height() / numHeightCells
        gridMap = HashMap<Pair<Int, Int>, MutableSet<CollidableEntity>>()
        invokeAllOrderedPairs(numWidthCells, numHeightCells) { i, j ->
            gridMap[Pair(i, j)] = mutableSetOf<CollidableEntity>()
        }
    }

    fun getCellEntities(key: Pair<Int, Int>): MutableSet<CollidableEntity>? {
        return gridMap[key]
    }

    fun cellsAreAdjacent(cellOne: Pair<Int, Int>, cellTwo: Pair<Int, Int>): Boolean {
        return (abs(cellOne.first - cellTwo.first) + abs(cellOne.second - cellTwo.second)) == 1
    }

    fun cellsAreDiagonal(cellOne: Pair<Int, Int>, cellTwo: Pair<Int, Int>): Boolean {
        return (abs(cellOne.first - cellTwo.first) == 1)
                && (abs(cellOne.second - cellTwo.second) == 1)
    }

    fun markGridCell(key: Pair<Int, Int>, item: CollidableEntity): Unit {
        if ((item != null) && (item !in gridMap[key]!!)) {
            gridMap[key]?.add(item)
        }
    }

    fun getKeyForPosition(position: Vector): Pair<Int, Int> {
        var xKey = floor((position.x-boundary.lowerBounds.x) / cellWidth).toInt()
        var yKey = ((position.y-boundary.lowerBounds.y) / cellHeight).toInt()
        if (xKey == numWidthCells) {xKey--}
        if (xKey < 0) xKey = 0
        if (yKey == numHeightCells) {yKey--}
        if (yKey < 0) yKey = 0
        return Pair(xKey, yKey)
    }

    fun getCollisionKeys(baseKey: Pair<Int, Int>): List<Pair<Int, Int>> {
        val outList = mutableListOf<Pair<Int, Int>>()
        outList.add(baseKey)
        if (baseKey.first > 0) {
            if (baseKey.second > 0) {
                outList.add(Pair(baseKey.first - 1, baseKey.second - 1))
            }
            outList.add(Pair(baseKey.first - 1, baseKey.second))
            if (baseKey.second < (numHeightCells - 1)) {
                outList.add(Pair(baseKey.first - 1, baseKey.second + 1))
            }
        }
        if (baseKey.second > 0) {
            outList.add(Pair(baseKey.first, baseKey.second - 1))
        }
        if (baseKey.second < (numHeightCells - 1)) {
            outList.add(Pair(baseKey.first, baseKey.second + 1))
        }
        if (baseKey.first < (numWidthCells - 1)) {
            if (baseKey.second > 0) {
                outList.add(Pair(baseKey.first + 1, baseKey.second - 1))
            }
            outList.add(Pair(baseKey.first + 1, baseKey.second))
            if (baseKey.second < (numHeightCells - 1)) {
                outList.add(Pair(baseKey.first + 1, baseKey.second + 1))
            }
        }
        return outList.toList()
    }

    fun unMarkGridCell(collisionGridCell: Pair<Int, Int>?, collidableEntity: CollidableEntity) {
        gridMap[collisionGridCell]?.remove(collidableEntity)
    }
}