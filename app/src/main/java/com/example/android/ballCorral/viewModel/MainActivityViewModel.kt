package com.example.android.ballCorral.viewModel

import androidx.lifecycle.ViewModel
import com.example.android.ballCorral.gameSimulation.EntitySimulationState
import com.example.android.ballCorral.gameSimulation.EntitySimulator
import com.example.android.ballCorral.paintableShapes.PaintableShapeList
import com.example.android.ballCorral.utility.Vector

class MainActivityViewModel() : ViewModel() {

    private var entitySim: EntitySimulator? = null
    private val playerBarrierBuffer = mutableListOf<Pair<Vector, Vector>>()

    fun resizeSpace(w: Int, h: Int) {

    }

    fun flushPlayerLineBuffer() {
        playerBarrierBuffer.forEach { barrier -> entitySim?.addPlayerBarrier(barrier) }
        playerBarrierBuffer.clear()
    }

    fun initialize(screenDims: Vector) {
        entitySim = entitySim ?: EntitySimulator()
        entitySim!!.initialize(screenDims)
    }

    fun stepModel(dt: Float): Unit {
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