package ch.bildspur.anna.renderer

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.light.LedArray
import processing.core.PGraphics
import processing.core.PShape

class SceneRenderer(val g: PGraphics, val ledArrays: List<LedArray>, val project: Project) : IRenderer {
    private val task = TimerTask(0, { render() }, "SceneRenderer")
    override val timerTask: TimerTask
        get() = task

    lateinit var rodShape: PShape


    override fun setup() {

    }

    override fun render() {

    }

    override fun dispose() {
    }
}