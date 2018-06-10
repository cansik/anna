package ch.bildspur.anna.renderer

import ch.bildspur.anna.controller.timer.TimerTask

interface IRenderer {
    val timerTask: TimerTask

    fun setup()

    fun render()

    fun dispose()
}