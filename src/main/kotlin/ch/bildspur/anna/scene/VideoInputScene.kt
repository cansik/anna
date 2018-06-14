package ch.bildspur.anna.scene

import ch.bildspur.anna.Sketch
import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.mapping.Fixture
import ch.bildspur.anna.mapping.LineFixture
import ch.bildspur.anna.mapping.PixelMapper
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.light.Led
import ch.bildspur.anna.renderer.VisualisationRenderer
import ch.bildspur.anna.util.translate
import processing.core.PConstants
import processing.core.PGraphics
import processing.core.PVector
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

class VideoInputScene(project : Project) : BaseScene(project) {
    private val task = TimerTask(40, { update() })

    override val name: String
        get() = "Video Input"

    override val timerTask: TimerTask
        get() = task

    // scene variables
    val syphon = Sketch.instance.syphon

    val visualisation = Sketch.instance.renderer.find { it is VisualisationRenderer } as VisualisationRenderer

    val mapper = PixelMapper()

    val ledToFixtureLookup : MutableMap<Led, Fixture> = mutableMapOf()

    val padding = PVector(120f, 80f)

    lateinit var buffer : PGraphics

    @Volatile var isPixelMappingThreadRunning = false

    private var pixelMappingThread = thread(false) {
        while (isPixelMappingThreadRunning)
        {
            if(isRunning && syphon.frame.width > 0) {
                // lazy init buffer
                if(!::buffer.isInitialized)
                    buffer = Sketch.instance.createGraphics(syphon.frame.width, syphon.frame.height, PConstants.JAVA2D)

                // copy frame to buffer
                syphon.frameLock.withLock {
                    buffer.beginDraw()
                    buffer.image(syphon.frame, 0f, 0f)
                    buffer.endDraw()
                }

                // update fixtures
                mapper.updateFixtures(buffer)
                buffer.removeCache(syphon.frame)
            }

            Thread.sleep(task.interval / 2)
        }
    }

    private fun createWeightFixtures()
    {
        val translation = PVector((visualisation.annWidth / 2f) + (padding.x / 2), (visualisation.annHeight / 2f) + + (padding.y / 2))

        network.weights.forEach {
            val led1Pos = visualisation.getLEDPosition(it.neuron1, it.ledIndex1).translate(translation)
            val led2Pos = visualisation.getLEDPosition(it.neuron2, it.ledIndex2).translate(translation)
            val center = PVector.lerp(led1Pos, led2Pos, 0.5f)

            val fixture1 = LineFixture(led1Pos, center, thickness = 5)
            val fixture2 = LineFixture(center, led2Pos, thickness = 5)

            mapper.fixtures.add(fixture1)
            mapper.fixtures.add(fixture2)

            ledToFixtureLookup[it.led1] = fixture1
            ledToFixtureLookup[it.led2] = fixture2
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

        ledToFixtureLookup.map { it.value }
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
        isPixelMappingThreadRunning = true
        pixelMappingThread.start()

        super.setup()
    }

    override fun update() {
        ledToFixtureLookup.forEach { led, fixture ->
            led.color.fade(fixture.color, 1.0f)
        }
    }

    override fun dispose() {
        if(isPixelMappingThreadRunning) {
            isPixelMappingThreadRunning = false
            pixelMappingThread.join()
        }
        super.dispose()
    }
}