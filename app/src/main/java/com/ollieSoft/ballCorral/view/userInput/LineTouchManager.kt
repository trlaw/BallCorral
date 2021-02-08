package com.ollieSoft.ballCorral.view.userInput

import android.view.MotionEvent
import com.ollieSoft.ballCorral.R
import com.ollieSoft.ballCorral.utility.Vector

class LineTouchManager(val lineCompleteCallback: (Pair<Vector, Vector>) -> Unit) {

    private var pointerList = mutableListOf<LinePointer>()
    private var minLineLength = R.string.MIN_PLAYER_LINE_LENGTH.toDouble()

    fun handleTouchEvent(event: MotionEvent) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> handleTouchDown(event,0)
            MotionEvent.ACTION_POINTER_DOWN -> handleTouchDown(event,event.actionIndex)
            MotionEvent.ACTION_MOVE -> handleTouchMove(event)
            MotionEvent.ACTION_POINTER_UP -> handleTouchUp(event,event.actionIndex)
            MotionEvent.ACTION_UP -> handleTouchUp(event,0)
        }
    }

    private fun handleTouchDown(event: MotionEvent, actionIndex: Int) {
        pointerList.add(
            LinePointer(
                event.getPointerId(actionIndex),
                Vector(event.getX(actionIndex).toDouble(), event.getY(actionIndex).toDouble())
            )
        )
    }

    private fun handleTouchMove(event: MotionEvent) {
        for (i in 0 until event.pointerCount) {
            val indexPointerId = event.getPointerId(i)
            val indexLinePointer =
                pointerList.find { it -> it.pointerId == indexPointerId }
            if (indexLinePointer != null) {
                val moveEndVector = Vector(event.getX(i).toDouble(), event.getY(i).toDouble())
                if (moveEndVector.minus(indexLinePointer.lineStart).mag() >= minLineLength) {
                    lineCompleteCallback(Pair(indexLinePointer.lineStart, moveEndVector))
                    indexLinePointer.lineStart = moveEndVector
                }
            }
        }
    }

    private fun handleTouchUp(event: MotionEvent, actionIndex: Int) {
        pointerList = pointerList.filter { linePointer ->
            linePointer.pointerId != event.getPointerId(actionIndex)
        } as MutableList<LinePointer>
    }
}