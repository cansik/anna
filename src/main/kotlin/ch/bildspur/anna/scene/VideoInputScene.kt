package ch.bildspur.anna.scene

import ch.bildspur.anna.Sketch
import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.mapping.PixelMapper
import ch.bildspur.anna.model.ann.Network
import ch.bildspur.anna.renderer.VisualisationRenderer
import kotlin.concurrent.thread

class VideoInputScene(network : Network) : BaseScene(network) {
    private val task = TimerTask(40, { update() })

    override val name: String
        get() = "Video Input Scene"

    override val timerTask: TimerTask
        get() = task

    // scene variables
    val syphon = Sketch.instance.syphon

    val visualisation = Sketch.instance.renderer.find { it is VisualisationRenderer } as VisualisationRenderer

    val mapper = PixelMapper()

    @Volatile var isRunning = false

    private var pixelMappingThread = thread {
        while (isRunning)
        {
            mapper.updateFixtures(syphon.frame)
            Thread.sleep(task.interval / 2)
        }
    }

    override fun setup() {
        // create pixel fixtures

        // start mapping thread
        isRunning = true
        pixelMappingThread.start()
    }

    override fun start() {

    }

    override fun update() {

    }

    override fun stop() {

    }

    override fun dispose() {
        if(isRunning) {
            isRunning = false
            pixelMappingThread.join()
        }
    }
}