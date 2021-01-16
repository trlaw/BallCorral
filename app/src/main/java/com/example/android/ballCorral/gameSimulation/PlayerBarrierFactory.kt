package com.example.android.ballCorral.gameSimulation

const val PLAYER_BARRIER_WIDTH = 4f

class PlayerBarrierFactory: BarrierFactory() {
    override fun create(): BarrierEntity {
        return BarrierEntity().apply {
            width = PLAYER_BARRIER_WIDTH
        }
    }
}