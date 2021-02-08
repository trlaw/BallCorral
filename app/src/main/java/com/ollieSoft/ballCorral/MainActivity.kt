@file:Suppress("DEPRECATION")

package com.ollieSoft.ballCorral

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.coroutineScope
import com.ollieSoft.ballCorral.databinding.ActivityMainBinding
import com.ollieSoft.ballCorral.utility.LimitedSpeedRunner
import com.ollieSoft.ballCorral.utility.getScreenDims
import com.ollieSoft.ballCorral.utility.setFullScreen
import com.ollieSoft.ballCorral.view.canvasPainter.FitContentPainter
import com.ollieSoft.ballCorral.view.canvasPainter.LayoutTransitionPainter
import com.ollieSoft.ballCorral.view.canvasPainter.ShapePainter
import com.ollieSoft.ballCorral.view.userInput.LineTouchManager
import com.ollieSoft.ballCorral.viewModel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var frameRunner: LimitedSpeedRunner
    private var shapePainter: ShapePainter? = null
    private var viewModel: MainActivityViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Databinding
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        //Set Full Screen
        setFullScreen(binding.mainBallView)

        //ViewModel Setup
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(
            MainActivityViewModel::class.java
        )
        viewModel?.initialize(getScreenDims(windowManager),resources)

        //Setup Touch Input on CanvasDrawView
        binding.mainBallView.lineTouchManager = LineTouchManager(viewModel!!::takePlayerLine)

        //Initialize and bind Painter to CanvasDrawView
        shapePainter = ShapePainter(resources)
        binding.mainBallView.framePainter =
            LayoutTransitionPainter(FitContentPainter(shapePainter!!))

        //Set view size change callback to notify ViewModel
        binding.mainBallView.sizeChangeCallback = { w, h -> viewModel?.resizeSpace(w, h) }

        //Start Simulation
        runSimulation()

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onRestart() {
        super.onRestart()
        setFullScreen(binding.mainBallView)
        runSimulation()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        super.onStop()
        frameRunner.endTask()
    }

    private fun runSimulation() {
        frameRunner = LimitedSpeedRunner(
            lifecycle.coroutineScope,
            R.string.FRAME_INTERVAL_MS.toLong(),
            ::backgroundWork,
            ::workCompleteCallback
        )
        frameRunner.startTask()
    }

    //Will run on separate thread once per frame
    private fun backgroundWork() {
        viewModel?.tryRunGame() //Sets game to running state if game is initialized
        viewModel?.stepModel(R.string.FRAME_SIMULATION_TIME.toDouble())
    }

    //Work complete action
    private fun workCompleteCallback() {
        shapePainter?.paintableShapeList = viewModel?.getDrawObjects()
        viewModel?.flushPlayerLineBuffer()
        binding.mainBallView.invalidate()
    }

}
