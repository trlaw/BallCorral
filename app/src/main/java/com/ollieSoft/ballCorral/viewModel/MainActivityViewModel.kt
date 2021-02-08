package com.ollieSoft.ballCorral.viewModel

import android.content.res.Resources
import androidx.lifecycle.ViewModel
import com.ollieSoft.ballCorral.gameSimulation.EntitySimulationState
import com.ollieSoft.ballCorral.gameSimulation.EntitySimulator
import com.ollieSoft.ballCorral.paintableShapes.PaintableShapeList
import com.ollieSoft.ballCorral.utility.Vector

class MainActivityViewModel() : ViewModel() {

    private var entitySim: EntitySimulator? = null
    private val playerBarrierBuffer = mutableListOf<Pair<Vector, Vector>>()

    fun resizeSpace(w: Int, h: Int) {

    }

    fun flushPlayerLineBuffer() {
        playerBarrierBuffer.forEach { barrier -> entitySim?.addPlayerBarrier(barrier) }
        playerBarrierBuffer.clear()
    }

    fun initialize(screenDims: Vector, resObj: Resources) {
        entitySim = entitySim ?: EntitySimulator()
        entitySim!!.initialize(screenDims, resObj)
    }

    fun stepModel(dt: Double): Unit {
        entitySim?.updateState(dt)
    }

    fun getDrawObjects(): PaintableShapeList? {
        return entitySim?.getPaintableObjects()
    }

    fun takePlayerLine(playerLine: Pair<Vector, Vector>) {
        playerBarrierBuffer.add(playerLine)
    }

    fun tryRunGame() {
        if (entitySim?.entitySimulationState == EntitySimulationState.INITIALIZED) {
            entitySim?.entitySimulationState = EntitySimulationState.RUNNING
        }
    }
}