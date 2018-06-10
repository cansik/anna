package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.light.LedArray

abstract class BaseScene(val ledArrays: List<LedArray>) {
    abstract val name: String

    abstract val timerTask: TimerTask

    abstract fun setup()
    abstract fun update()
    abstract fun stop()
    abstract fun dispose()
}