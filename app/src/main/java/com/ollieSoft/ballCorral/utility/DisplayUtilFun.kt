package com.ollieSoft.ballCorral.utility

import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager

fun setFullScreen(view: View) {
    view.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
}

fun getScreenDims(windowManager: WindowManager): Vector {
    val realMetrics = DisplayMetrics()
    windowManager.defaultDisplay.getRealMetrics(realMetrics)
    return Vector(realMetrics.widthPixels.toDouble(), realMetrics.heightPixels.toDouble())
}