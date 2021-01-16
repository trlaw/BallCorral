@file:Suppress("DEPRECATION")

package com.example.android.ballCorral

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.example.android.ballCorral.databinding.ActivityMainBinding
import com.example.android.ballCorral.utility.LimitedSpeedRunner
import com.example.android.ballCorral.utility.Vector
import com.example.android.ballCorral.view.CirclePaintFactory
import com.example.android.ballCorral.view.LinePaintFactory
import com.example.android.ballCorral.view.LineTouchManager
import com.example.android.ballCorral.view.TextPaintFactory
import com.example.android.ballCorral.view.canvasPainter.FitContentPainter
import com.example.android.ballCorral.view.canvasPainter.LayoutTransitionPainter
import com.example.android.ballCorral.view.canvasPainter.ShapePainter
import com.example.android.ballCorral.viewModel.MainActivityViewModel


//Minimum interval between frames (state update + frame buffer redraw)
const val FRAME_INTERVAL_MS: Long = 17

//Simulation time (dimensionless) to advance per frame
const val FRAME_SIM_TIME: Float = 1f

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var frameRunner: LimitedSpeedRunner
    private lateinit var lineTouchManager: LineTouchManager
    private var viewModel: MainActivityViewModel? = null
    private var shapePainter: ShapePainter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Databinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Set Full Screen
        setFullScreen()

        //ViewModel Setup
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainActivityViewModel::class.java
        )
        viewModel?.initialize(getScreenDims())

        //Setup Touch Input
        lineTouchManager = LineTouchManager(viewModel!!::takePlayerLine)
        binding.mainBallView.touchEventHandler = lineTouchManager::handleTouchEvent

        //Painter setup
        createPainter()

        //Set view painter
        binding.mainBallView.framePainter =
            LayoutTransitionPainter(FitContentPainter(shapePainter!!))

        //View to ViewModel Connections
        binding.mainBallView.sizeChangeCallback = ::resizeHandler

        //Start Simulation
        runSimulation()

    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
        frameRunner.endTask()
    }

    override fun onRestart() {
        super.onRestart()
        setFullScreen()
        runSimulation()
    }

    private fun setFullScreen() {
        binding.mainBallView.systemUiVisibility = (SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or SYSTEM_UI_FLAG_FULLSCREEN or SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun runSimulation() {
        frameRunner = LimitedSpeedRunner(
            lifecycle.coroutineScope,
            FRAME_INTERVAL_MS,
            ::backgroundWork,
            ::workCompleteCallback
        )
        frameRunner.startTask()
    }

    private fun resizeHandler(w: Int, h: Int): Unit {
        //Provide new dimensions to viewModel
        viewModel?.resizeSpace(w, h)
    }

    private fun createPainter() {
        //Canvas Painter Setup
        shapePainter = ShapePainter()
        shapePainter?.circlePaintFactory = CirclePaintFactory(
            resources.getIntArray(R.array.ballColors),
            resources.getColor(R.color.colorForeground)
        )
        shapePainter?.linePaintFactory =
            LinePaintFactory(resources.getColor(R.color.colorForeground))
        shapePainter?.textPaintFactory = TextPaintFactory()
    }

    //Will run on separate thread once per frame
    private fun backgroundWork() {
        viewModel?.tryRunGame() //Sets game to running state if game is initialized
        viewModel?.stepModel(FRAME_SIM_TIME)
    }

    //Work complete action
    private fun workCompleteCallback() {
        shapePainter?.paintableShapeList = viewModel?.getDrawObjects()
        shapePainter?.assignPaintFactories()
        viewModel?.flushPlayerLineBuffer()
        binding.mainBallView.invalidate()
    }

    private fun getScreenDims(): Vector {
        val realMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getRealMetrics(realMetrics)
        return Vector(realMetrics.widthPixels.toFloat(),realMetrics.heightPixels.toFloat())
    }

}
