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
import processing.core.PConstants
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

    val padding = PVector(120f, 80f)

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
        val translation = PVector((visualisation.annWidth / 2f) + (padding.x / 2), (visualisation.annHeight / 2f) + + (padding.y / 2))

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
        val map = Sketch.instance.createGraphics((visualisation.annWidth + padding.x).toInt(), (visualisation.annHeight + padding.y).toInt())
        map.beginDraw()
        map.background(0f, 0f)
        map.strokeWeight(1f)

        map.noFill()

        map.pushMatrix()
        map.stroke(0f, 255f, 0f, 230f)
        map.translate(padding.x / 2, padding.y / 2)
        map.rect(0f, 0f, visualisation.annWidth, visualisation.annHeight)
        map.popMatrix()


        map.stroke(0f, 255f, 0f, 230f)

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
        if(Sketch.instance.pixelDensity > 1)
            throw Exception("Pixel density has to be 1 for syphon input!")

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