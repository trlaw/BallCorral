package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import com.ollieSoft.ballCorral.gameSimulation.BarrierEntity

class GameEndingBarrierEntity : BarrierEntity() {
    //var gameOverCallback: (() -> Unit) = { }
    var ballRemoveCallback: ((GameEntity) -> Unit) = {}
    override fun handleCollision(otherEntity: CollidableEntity) {
        when(otherEntity) {
            is BallEntity -> ballRemoveCallback(otherEntity)
            else -> return
        }
    }
}