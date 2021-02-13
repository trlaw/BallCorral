package com.ollieSoft.ballCorral.gameSimulation.constraintSolver

import com.ollieSoft.ballCorral.gameSimulation.GameState

abstract class AbstractConstraint(private val inactiveTimeout: Double) {

    //Should accumulate as steps processed
    protected var timeSinceLastActive = 0.0

    //May be purged if timedOut
    fun timedOut(): Boolean {
        return timeSinceLastActive >= inactiveTimeout
    }

    //Parent algorithm determines when time actually advanced, must call this to ensure stale
    //constraints can be identified for purging.  Inheritors should also reset any computations
    //that should occur only once per time step (i.e. evaluation of initial velocity at start of
    //time step)
    fun age(dt: Double) {
        timeSinceLastActive += dt
    }

    fun resetLifeTimeCounter() {
        timeSinceLastActive = 0.0
    }

    //If constraint is satisfied, will not apply correction this iteration
    abstract fun satisfied(): Boolean

    //Evaluates constraint function
    abstract fun evalC(): Double

    //Evaluates first derivative of constraint function
    abstract fun evalCdot(): Double

    //Compute corrective impulse VectorN.  Should reset timeSinceLastActive to 0.0.
    abstract fun evalImpulses(dt: Double)

    //Apply computed corrective impulses to affected objects
    abstract fun applyCorrectiveImpulses()

    //Signals the start of time step for constraints which depend on initial quantities
    abstract fun resetInitialQuantities(gameState: GameState)
}