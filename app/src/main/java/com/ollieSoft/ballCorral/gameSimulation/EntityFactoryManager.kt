package com.ollieSoft.ballCorral.gameSimulation

import android.content.res.Resources
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.BallEntityFactory
import com.ollieSoft.ballCorral.gameSimulation.gameEntities.PlayerBarrierFactory

class EntityFactoryManager(resObj: Resources) {
    val ballFactory = BallEntityFactory(resObj)
    val playerBarrierFactory = PlayerBarrierFactory()
}