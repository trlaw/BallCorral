package com.ollieSoft.ballCorral.gameSimulation.constraintSolver

import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.gameSimulation.BarrierEntity
import com.ollieSoft.ballCorral.gameSimulation.CollisionGrid
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.CollidableEntity
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.GameEntity

const val ITERATIONS_PER_CALL = 2
const val IDLE_CONSTRAINT_LIFETIME = 20.0

class ConstraintSolver {

    private val iterationsPerCall = R.integer.CONSTRAINT_ITERATIONS
    private val idleConstraintLifetime = R.string.IDLE_CONSTRAINT_LIFETIME.toDouble()
    private val constraintCache =
        HashMap<Pair<CollidableEntity, CollidableEntity>, AbstractConstraint>()

    fun updateVelocities(collisionGrid: CollisionGrid, gameEntities: List<GameEntity>, dt: Double) {

        addRequiredConstraints(collisionGrid, gameEntities)
        constraintCache.onEach { it.value.resetInitialQuantities() }
        repeat(iterationsPerCall) {
            var constraintsToApply = mutableListOf<AbstractConstraint>()
            constraintCache.onEach {
                if (!(it.value.satisfied())) {
                    constraintsToApply.add(it.value)
                }
            }
            constraintsToApply.forEach { it.evalImpulses(dt) }
            constraintsToApply.forEach { it.applyCorrectiveImpulses() }
        }
        ageAndPurgeIdles(dt)
    }

    private fun ageAndPurgeIdles(dt: Double) {
        val keysOfEntriesToPurge = mutableListOf<Pair<CollidableEntity, CollidableEntity>>()
        constraintCache.onEach {
            it.value.age(dt)
            if (it.value.timedOut()) {
                keysOfEntriesToPurge.add(it.key)
            }
        }
        keysOfEntriesToPurge.forEach {
            constraintCache.remove(it)
        }
    }

    private fun addRequiredConstraints(
        collisionGrid: CollisionGrid, gameEntities: List<GameEntity>
    ) {
        gameEntities.forEach { gameEntity ->
            if (gameEntity is BallEntity) {
                gameEntity.getPotentialColliders(collisionGrid).forEach {
                    when (it) {
                        is BallEntity -> addConstraint(gameEntity, it)
                        is BarrierEntity -> addConstraint(gameEntity, it)
                    }
                }
            }
        }
    }

    private fun cacheContainsPair(
        collidableOne: CollidableEntity,
        collidableTwo: CollidableEntity
    ): Boolean {
        return (
                constraintCache.containsKey(Pair(collidableOne, collidableTwo)))
                || (constraintCache.containsKey(Pair(collidableTwo, collidableOne)))
    }

    //Constraint addition overloads
    private fun addConstraint(ballEntityOne: BallEntity, ballEntityTwo: BallEntity) {
        if (!cacheContainsPair(ballEntityOne, ballEntityTwo)) {
            constraintCache[Pair(ballEntityOne, ballEntityTwo)] = BallBallConstraint(
                ballEntityOne, ballEntityTwo, idleConstraintLifetime
            )
        }
    }

    private fun addConstraint(barrierEntity: BarrierEntity, ballEntity: BallEntity) {
        if (!cacheContainsPair(barrierEntity, ballEntity)) {
            constraintCache[Pair(barrierEntity, ballEntity)] = BallBarrierConstraint(
                ballEntity, barrierEntity, idleConstraintLifetime
            )
        }
    }

    private fun addConstraint(ballEntity: BallEntity, barrierEntity: BarrierEntity) {
        addConstraint(barrierEntity, ballEntity)
    }
}