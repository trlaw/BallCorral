package com.ollieSoft.ballCorral.gameSimulation

import android.content.res.Resources
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BestScoreEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.PaintableEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.ScoreEntity
import com.ollieSoft.ballCorral.paintableShapes.PaintableShapeList

class GameState(resObj: Resources, private val gameBoundary: GameBoundary) {

    private val bestScoreEntity = BestScoreEntity()
    val collisionGrid = CollisionGrid(gameBoundary)
    val entityFactoryManager = EntityFactoryManager(resObj)
    val gameEntityList = GameEntityList()
    var gameTime = 0.0
    val playerBarrierList = PlayerBarrierList()
    private val scoreEntity = ScoreEntity()

    init {
        gameEntityList.addEntity(scoreEntity)
    }

    var score: Long
        get() {
            return scoreEntity.score
        }
        set(value) {
            scoreEntity.score = value
            if (value > bestScoreEntity.bestScore) {
                bestScoreEntity.bestScore = value
            }
        }

    fun getPaintableObjects(): PaintableShapeList {
        val outputList = PaintableShapeList()
        outputList.shapesUpperLeft = gameBoundary.lowerBounds
        outputList.shapesLowerRight = gameBoundary.upperBounds
        gameEntityList.forEach { gameEntity ->
            if (gameEntity is PaintableEntity) {
                outputList.items.add(gameEntity.getPaintableShape())
            }
        }
        return outputList
    }
}