package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.light.LedArray
import ch.bildspur.anna.util.ColorMode
import ch.bildspur.anna.util.forEachLED

class BlackScene(ledArrays: List<LedArray>) : BaseScene(ledArrays) {
    private val task = TimerTask(1000, { update() })

    override val name: String
        get() = "Black Scene"

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        // set all led's one black
        ledArrays.forEachLED {
            it.color.fade(ColorMode.color(0), 0.05f)
        }
    }

    override fun update() {
    }

    override fun stop() {
    }

    override fun dispose() {
    }
}