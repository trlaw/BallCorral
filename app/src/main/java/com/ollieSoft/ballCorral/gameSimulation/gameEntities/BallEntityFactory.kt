package com.ollieSoft.ballCorral.gameSimulation.gameEntities

import android.content.res.Resources
import com.ollieSoft.ballCorral.R

open class BallEntityFactory(resObj: Resources) {

    private val numColors = resObj.getIntArray(R.array.ballColors).size
    var nextColorIndex = 0
    open fun create(): BallEntity {
        return BallEntity().apply {
            this.colorIndex = nextColorIndex++
            nextColorIndex %= numColors
        }
    }

}