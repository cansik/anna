package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.ann.Network

abstract class BaseScene(val project : Project) {
    val network : Network = project.network

    var isRunning = false
    var isInitialised = false

    abstract val name: String

    abstract val timerTask: TimerTask

    open fun setup()
    {
        isInitialised = true
    }

    open fun start()
    {
        isRunning = true
    }

    abstract fun update()

    open fun stop()
    {
        isRunning = false
    }

    open fun dispose()
    {
        isInitialised = false
    }
}