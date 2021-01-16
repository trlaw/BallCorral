package com.example.android.ballCorral.gameSimulation

import com.example.android.ballCorral.paintableShapes.PaintableShapeList
import com.example.android.ballCorral.utility.Vector


const val BALL_ADD_TIME = 100f
const val BALL_LIMIT = 50
const val MAX_BARRIERS = 50
const val MAX_LOST_BALLS = 10

class EntitySimulator() {

    private val gameBallFactory: BallEntityFactory = BallEntityFactory()
    private val playerBarrierFactory: BarrierFactory = PlayerBarrierFactory()
    private val playerBarrierList: MutableList<BarrierEntity> = mutableListOf<BarrierEntity>()
    private val scoreTextEntity = ScoreTextEntity()
    private val lostBallsTextEntity = LostBallsTextEntity()
    private val bestScoreTextEntity = BestScoreTextEntity()
    private lateinit var collisionGrid: CollisionGrid
    private val entityList = mutableListOf<GameEntity>()
    var entitySimulationState = EntitySimulationState.NONE
    private var gameBoundary: GameBoundary? = null
    private var entitiesToRemove = mutableSetOf<GameEntity>()
    private var restartFlag: Boolean = false
    var simTime = 0f


    //Game Simulation Lifecycle Methods

    fun endGame() {
        entitySimulationState = EntitySimulationState.ENDED
    }

    fun initialize(screenDims: Vector? = null) {
        entitySimulationState = EntitySimulationState.NONE

        if (!trySetupGameBoundary(screenDims)) {
            return
        }

        resetEntityLists()
        setupScoringEntities()
        initCollisionGrid()
        addGameEndingBarriers()

        entitySimulationState = EntitySimulationState.INITIALIZED
    }

    private fun restartIfRequested() {
        if (restartFlag) {
            restartFlag = false
            initialize()
        }
    }

    private fun scheduleRestart() {
        restartFlag = true
    }

    fun updateState(dt: Float) {

        if (entitySimulationState == EntitySimulationState.RUNNING) {

            //Add ball to game if spawn conditions satisfied
            tryAddBall()

            //Advance mobile object positions
            updateMobileEntityPositions(dt)

            //Populate collision grid with mobile objects
            markCollisionGridWithMobileEntities()

            //Adjust mobile objects according to collisions
            processMobileEntityCollisions()

            //Remove mobile entities from collision grid since position may change on next step
            unMarkMobileEntitiesFromGrid()

            removeMarkedEntities() //Remove entity list items marked for removal

            simTime += dt
        }
        restartIfRequested()
    }


    //State Update Helper Methods
    private fun markCollisionGridWithMobileEntities() {
        var i = 0
        while (i < entityList.size) {
            if ((entityList[i] is CollidableEntity) && (entityList[i] is MobileEntity)) {
                (entityList[i] as CollidableEntity).markCollisionGrid(collisionGrid)
            }
            i++
        }
    }

    private fun processMobileEntityCollisions() {
        entityList.forEach { it ->
            if (it is MobileEntity) {
                it.reactToCollisions(collisionGrid)
            }
        }
    }

    private fun unMarkMobileEntitiesFromGrid() {
        for (gameEntity in entityList) {
            if ((gameEntity is CollidableEntity) && (gameEntity is MobileEntity)) {
                gameEntity.unMarkCollisionGrid(collisionGrid)
            }
        }
    }

    private fun updateMobileEntityPositions(dt: Float) {
        entityList.forEach { it ->
            if (it is MobileEntity) {
                it.travel(dt)
            }
        }
    }

    //Entity Adders
    fun addPlayerBarrier(barrier: Pair<Vector, Vector>) {
        if (entitySimulationState == EntitySimulationState.RUNNING) {
            tryAddBarrier(barrier)
        }
    }

    private fun tryAddBarrier(barrier: Pair<Vector, Vector>) {
        val newBarrier = playerBarrierFactory.create().apply {
            start = barrier.first
            end = barrier.second
        }
        //Disallow barriers in contact with ball
        if (entityList.none { gameEntity ->
                ((gameEntity is BallEntity) && (gameEntity.collided(newBarrier)))
            }) {
            addEntity(newBarrier)
            playerBarrierList.add(newBarrier)
            if (playerBarrierList.size > MAX_BARRIERS) {
                markForRemoval(playerBarrierList.removeAt(0))
            }
        }
    }

    private fun tryAddBall() {

        //Add ball only if enough time elapsed
        if (simTime < (getScore() * BALL_ADD_TIME)) {
            return
        }

        //Don't add if already at maximum allowed ball count
        if ((entityList.count { it -> it is BallEntity }) >= BALL_LIMIT) {
            return
        }

        val newBall = gameBallFactory.create()
        gameBoundary!!.setSpawnState(newBall)

        //Add ball only if it will not collide with an existing entity
        if (entityList.none { gameEntity ->
                ((gameEntity is CollidableEntity) && (gameEntity.collided(newBall)))
            }) {
            addEntity(newBall)
            incrementScore()
        }
    }

    //General GameEntity Lifecycle Methods
    private fun addEntity(gameEntity: GameEntity) {
        //Mark collision grid with static element if necessary
        if ((gameEntity is CollidableEntity) && !(gameEntity is MobileEntity)) {
            gameEntity.markCollisionGrid(collisionGrid)
        }
        entityList.add(gameEntity)
    }

    private fun markForRemoval(gameEntity: GameEntity) {
        if ((gameEntity is CollidableEntity) && (gameEntity !is MobileEntity)) {
            gameEntity.unMarkCollisionGrid(collisionGrid)
        }
        if (gameEntity is BallEntity) {
            scoreTextEntity.score--
            lostBallsTextEntity.lostBallCount++
            if (lostBallsTextEntity.lostBallCount > MAX_LOST_BALLS) {
                scheduleRestart()
            }
        }
        entitiesToRemove.add(gameEntity)
    }

    private fun removeMarkedEntities() {
        entityList.removeAll(entitiesToRemove)
        entitiesToRemove.clear()
    }

    //Score Handling Methods
    private fun getScore(): Int {
        scoreTextEntity.score = entityList.count { gameEntity -> gameEntity is BallEntity }
        return scoreTextEntity.score
    }

    private fun incrementScore() {
        scoreTextEntity.score++
        if (scoreTextEntity.score > bestScoreTextEntity.bestScore) {
            bestScoreTextEntity.bestScore = scoreTextEntity.score
        }
    }


    //Accessors

    fun getPaintableObjects(): PaintableShapeList {
        val outputList = PaintableShapeList()
        if ((gameBoundary != null) && (gameBoundary!!.isValidGameBoundary())) {
            outputList.shapesUpperLeft = gameBoundary!!.lowerBound
            outputList.shapesLowerRight = gameBoundary!!.upperBound
            entityList.forEach { gameEntity ->
                if (gameEntity is PaintableEntity) {
                    outputList.items.add(gameEntity.getPaintableShape())
                }
            }
        }
        return outputList
    }

    //Initialization Methods

    private fun addGameEndingBarriers() {
        //Just the outsides
        val cornersArray = arrayOf<Vector>(
            gameBoundary!!.lowerBound,
            Vector(gameBoundary!!.upperBound.x, gameBoundary!!.lowerBound.y),
            gameBoundary!!.upperBound,
            Vector(gameBoundary!!.lowerBound.x, gameBoundary!!.upperBound.y)
        )
        for (i in 0 until 4) {
            addEntity(GameEndingBarrierEntity().apply {
                start = cornersArray[i]
                end = cornersArray[(i + 1) % 4]
                ballRemoveCallback = ::markForRemoval
            })
        }
    }

    private fun initCollisionGrid() {
        //maxEntitySize is that for mobile+collidable objects and should be the maximum distance
        //between any two points on an entity.  Argument source must be revised if add any more
        //mobile+collidable objects, or BallEntities no longer have common radius
        //collisionGrid = CollisionGrid(gameBoundary!!, (2f) * gameBallFactory.create().radius)
        collisionGrid =
            CollisionGrid(gameBoundary!!, (2f) * BallEntityFactory().create().radius)
    }

    private fun resetEntityLists() {
        entityList.clear()
        playerBarrierList.clear()
    }

    private fun setupScoringEntities() {
        addEntity(lostBallsTextEntity)
        addEntity(scoreTextEntity)
        addEntity(bestScoreTextEntity)
        lostBallsTextEntity.lostBallCount = 0
        simTime = 0f
    }

    private fun trySetupGameBoundary(boundaryDims: Vector? = null): Boolean {
        if ((gameBoundary == null) || (!(gameBoundary!!.isValidGameBoundary()))) {
            if (boundaryDims != null) {
                gameBoundary = GameBoundary(boundaryDims)
            } else {
                return false
            }
            if (gameBoundary!!.isValidGameBoundary()) {
                return true
            }
            return false
        }
        return true
    }
}

enum class EntitySimulationState {
    NONE, INITIALIZED, RUNNING, PAUSED, ENDED
}