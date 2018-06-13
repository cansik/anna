package ch.bildspur.anna.scene

import ch.bildspur.anna.Sketch
import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.mapping.Fixture
import ch.bildspur.anna.mapping.LineFixture
import ch.bildspur.anna.mapping.PixelMapper
import ch.bildspur.anna.model.ann.Network
import ch.bildspur.anna.model.ann.Weight
import ch.bildspur.anna.renderer.VisualisationRenderer
import ch.bildspur.anna.util.translate
import processing.core.PApplet
import processing.core.PVector
import kotlin.concurrent.thread
import kotlin.math.roundToInt

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

    val weightToFixtureLookup : MutableMap<Weight, Fixture> = mutableMapOf()

    @Volatile var isRunning = false

    private var pixelMappingThread = thread(false) {
        while (isRunning)
        {
            mapper.updateFixtures(syphon.frame)
            Thread.sleep(task.interval / 2)
        }
    }

    private fun createWeightFixtures()
    {
        val translation = PVector(visualisation.annWidth / 2f, visualisation.annHeight / 2f)

        network.weights.forEach {
            val led1Pos = visualisation.getLEDPosition(it.neuron1, it.ledIndex1).translate(translation)
            val led2Pos = visualisation.getLEDPosition(it.neuron2, it.ledIndex2).translate(translation)

            val fixture = LineFixture(led1Pos, led2Pos, thickness = 5)

            mapper.fixtures.add(fixture)
            weightToFixtureLookup[it] = fixture
        }
    }

    private fun createMap()
    {
        val map = Sketch.instance.createGraphics(visualisation.annWidth.toInt(), visualisation.annHeight.toInt(), PApplet.JAVA2D)
        map.beginDraw()
        map.background(0)
        map.noFill()
        map.stroke(0f, 255f, 0f, 230f)
        map.strokeWeight(1f)

        weightToFixtureLookup.map { it.value }
                .filterIsInstance<LineFixture>()
                .forEach {fixture ->
                    fixture.subFixtures.forEach {
                        map.rect(it.x.toFloat(), it.y.toFloat(), it.width.toFloat(), it.height.toFloat())
                    }
        }

        map.endDraw()
        map.save("data/fixture_map.png")
    }

    override fun setup() {
        createWeightFixtures()
        createMap()

        // start mapping thread
        isRunning = true
        pixelMappingThread.start()
    }

    override fun start() {

    }

    override fun update() {
        weightToFixtureLookup.forEach { weight, fixture ->
            weight.led1.color.fade(fixture.color, 0.5f)
            weight.led2.color.fade(fixture.color, 0.5f)
        }
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