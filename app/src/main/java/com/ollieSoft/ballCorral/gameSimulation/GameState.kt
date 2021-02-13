package com.ollieSoft.ballCorral.gameSimulation

import android.content.res.Resources
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BestScoreEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.PaintableEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.ScoreEntity
import com.ollieSoft.ballCorral.paintableShapes.PaintableShapeList
import com.ollieSoft.ballCorral.utility.Vector

class GameState(resObj: Resources, val gameBoundary: GameBoundary) {

    private val bestScoreEntity = BestScoreEntity()
    val collisionGrid = CollisionGrid(gameBoundary)
    val entityFactoryManager = EntityFactoryManager(resObj)
    val gameEntityList = GameEntityList()
    var gameTime = 0.0
    private val playerBarrierList = PlayerBarrierList()
    private val scoreEntity = ScoreEntity()

    init {
        gameEntityList.addEntity(scoreEntity)
        gameEntityList.addEntity(bestScoreEntity)
        GameDifficultyMap.initializeStatics(resObj)
    }

    var score: Long
        get() {
            return scoreEntity.score
        }
        set(value) {
            scoreEntity.score = if (value > 0) value else 0
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
                outputList.add(gameEntity.getPaintableShape(this))
            }
        }
        return outputList
    }

    //Restart game
    fun reset() {
        bestScoreEntity.bestScore = 0
        collisionGrid.clearGrid()
        entityFactoryManager.reset()
        gameEntityList.clear()
        gameTime = 0.0
        playerBarrierList.reset()
        scoreEntity.score = 0
    }

    fun tryAddBarrier(barrierEnds: Pair<Vector, Vector>) {
        val newBarrier =
            entityFactoryManager.playerBarrierFactory.create(barrierEnds.first, barrierEnds.second)
        if (gameEntityList.none { gameEntity ->
                ((gameEntity is BallEntity) && (gameEntity.collided(newBarrier)))
            }) {
            gameEntityList.addEntity(newBarrier)
            playerBarrierList.add(newBarrier,this)
        }
    }

}
