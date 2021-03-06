package ch.bildspur.anna

import ch.bildspur.anna.io.ArtNetConnection
import ch.bildspur.anna.controller.PeasyController
import ch.bildspur.anna.controller.timer.Timer
import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.event.Event
import ch.bildspur.anna.io.IOConnection
import ch.bildspur.anna.io.SyphonInput
import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.renderer.ArtNetRenderer
import ch.bildspur.anna.renderer.IRenderer
import ch.bildspur.anna.renderer.VisualisationRenderer
import ch.bildspur.anna.scene.SceneManager
import ch.bildspur.anna.util.LogBook
import ch.bildspur.anna.util.draw
import ch.bildspur.anna.util.forEachNode
import ch.bildspur.anna.util.format
import ch.bildspur.postfx.builder.PostFX
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics
import processing.opengl.PJOGL
import javax.swing.Renderer


/**
 * Created by cansik on 04.02.17.
 */
class Sketch : PApplet() {
    companion object {
        @JvmStatic
        val HIGH_RES_FRAME_RATE = 60f
        @JvmStatic
        val LOW_RES_FRAME_RATE = 30f

        @JvmStatic
        val WINDOW_WIDTH = 1024
        @JvmStatic
        val WINDOW_HEIGHT = 768

        @JvmStatic
        val CURSOR_HIDING_TIME = 1000L * 5L

        @JvmStatic
        val NAME = "ANNA"

        @JvmStatic
        val URI_NAME = "anna"

        @JvmStatic
        val VERSION = "0.1"

        @JvmStatic
        lateinit var instance: Sketch

        @JvmStatic
        fun map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double): Double {
            return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
        }
    }


    @Volatile
    var isInitialised = DataModel(false)

    var afterRenderReset = Event<Any>()

    var fpsOverTime = 0f

    @Volatile
    var isResetRendererProposed = false

    var isRendering = DataModel(true)

    val peasy = PeasyController(this)

    val timer = Timer()

    val artnet = ArtNetConnection()

    val syphon = SyphonInput(this)

    lateinit var canvas: PGraphics

    var lastCursorMoveTime = 0

    val renderer = mutableListOf<IRenderer>()

    val connections = listOf<IOConnection>(syphon)

    val project = DataModel(Project())

    lateinit var fx: PostFX

    init {
    }

    override fun settings() {
        if (project.value.isFullScreenMode.value)
            fullScreen(PConstants.P3D, project.value.fullScreenDisplay.value)
        else
            size(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)

        PJOGL.profile = 1
        smooth()

        // retina screen
        if (project.value.highResMode.value)
            pixelDensity = 2
    }

    override fun setup() {
        Sketch.instance = this

        // vsync
        if(project.value.vsyncMode.value) {
            frameRate(1000f)

            val pgl = beginPGL() as PJOGL
            pgl.gl.swapInterval = 1
            endPGL()
        }
        else
            frameRate(if (project.value.highFPSMode.value) HIGH_RES_FRAME_RATE else LOW_RES_FRAME_RATE)

        colorMode(HSB, 360f, 100f, 100f)

        project.onChanged += {
            onProjectChanged()
        }
        project.fire()

        fx = PostFX(this)
        peasy.setup()
        artnet.open()


        // setup connections
        connections.forEach { it.setup() }

        // timer for cursor hiding
        timer.addTask(TimerTask(CURSOR_HIDING_TIME, {
            val current = millis()
            if (current - lastCursorMoveTime > CURSOR_HIDING_TIME)
                noCursor()
        }, "CursorHide"))

        LogBook.log("Start")
    }

    override fun draw() {
        background(5)

        if (skipFirstFrames())
            return

        // setup long loading controllers
        if (initControllers())
            return

        // reset renderer if needed
        if (isResetRendererProposed)
            resetRenderer()

        // update ledArrays
        updateLEDColors()

        // update connections
        connections.forEach { it.update() }

        canvas.draw {
            it.background(5)

            // render (update timer)
            if (isRendering.value)
                timer.update()

            peasy.applyTo(canvas)
        }

        // add hud
        peasy.hud {
            // output image
            if (project.value.highResMode.value)
                fx.render(canvas)
                        .bloom(0.0f, 20, 40f)
                        .compose()
            else
                image(canvas, 0f, 0f)

            // render syphon frame

            if(project.value.syphonSettings.showSyphonInput.value)
                image(syphon.frame, 50f, 50f, syphon.frame.width / 2f, syphon.frame.height / 2f)

            drawFPS(g)
        }
    }

    fun proposeResetRenderer() {
        if (isInitialised.value) {
            isResetRendererProposed = true
        }
    }

    private fun onProjectChanged() {
        surface.setTitle("$NAME ($VERSION) - ${project.value.name.value}")

        // setup hooks
        setupHooks()
    }

    private fun setupHooks() {
        project.value.nodes.forEach {
            it.address.onChanged.clear()
            it.address.onChanged += {
                proposeResetRenderer()
            }
        }
    }

    private fun updateLEDColors() {
        project.value.network.forEachNode {
            it.ledArray.leds.forEach {
                it.color.update()
            }
        }
    }

    private fun resetRenderer() {
        println("resetting renderer...")

        renderer.forEach {
            timer.taskList.remove(it.timerTask)
            it.dispose()
        }

        renderer.clear()

        // add renderer
        renderer.add(VisualisationRenderer(project.value, canvas))
        renderer.add(ArtNetRenderer(project.value, artnet))
        renderer.add(SceneManager(project.value))

        isResetRendererProposed = false

        // rebuild
        // setup renderer
        renderer.forEach {
            it.setup()
            timer.addTask(it.timerTask)
        }

        // renderer resetted
        afterRenderReset.invoke(renderer)
    }

    private fun resetCanvas() {
        canvas = createGraphics(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.P3D)

        // retina screen
        if (project.value.highResMode.value)
            canvas.pixelDensity = 2
    }

    private fun skipFirstFrames(): Boolean {
        // skip first two frames
        if (frameCount < 2) {
            peasy.hud {
                textAlign(CENTER, CENTER)
                fill(255)
                textSize(20f)
                text("${Sketch.NAME} is loading...", width / 2f, height / 2f)
            }
            return true
        }

        return false
    }

    private fun initControllers(): Boolean {
        if (!isInitialised.value) {
            resetCanvas()

            timer.setup()

            prepareExitHandler()

            // setting up renderer
            resetRenderer()

            // open connections
            connections.forEach { it.open() }

            isInitialised.value = true
            return true
        }

        return false
    }

    private fun drawFPS(pg: PGraphics) {
        // draw fps
        fpsOverTime += frameRate
        val averageFPS = fpsOverTime / frameCount.toFloat()

        pg.textAlign(PApplet.LEFT, PApplet.BOTTOM)
        pg.fill(255)
        pg.textSize(12f)
        pg.text("FPS: ${frameRate.format(2)}\nFOT: ${averageFPS.format(2)}", 10f, height - 5f)
    }

    private fun prepareExitHandler() {
        Runtime.getRuntime().addShutdownHook(Thread {
            println("shutting down...")
            renderer.forEach { it.dispose() }

            println("closing artnet...")
            artnet.close()

            // open connections
            println("closing connections...")
            connections.forEach { it.close() }

            LogBook.log("Stop")
            println("done!")
        })
    }

    override fun mouseMoved() {
        super.mouseMoved()
        cursor()
        lastCursorMoveTime = millis()
    }
}