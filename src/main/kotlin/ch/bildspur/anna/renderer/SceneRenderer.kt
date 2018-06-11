package ch.bildspur.anna.renderer

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.light.LedArray
import processing.core.PGraphics
import processing.core.PShape

class SceneRenderer(val project: Project, val g: PGraphics) : IRenderer {
    private val task = TimerTask(0, { render() }, "SceneRenderer")
    override val timerTask: TimerTask
        get() = task


    override fun setup() {

    }

    override fun render() {
        g.fill(255)
        g.box(100f)
    }

    override fun dispose() {
    }
}