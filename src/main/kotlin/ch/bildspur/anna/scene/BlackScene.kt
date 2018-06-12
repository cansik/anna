package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.ann.Network
import ch.bildspur.anna.util.ColorMode
import ch.bildspur.anna.util.forEachNode

class BlackScene(network : Network) : BaseScene(network) {
    private val task = TimerTask(1000, { update() })

    override val name: String
        get() = "Black Scene"

    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        // set all led's one black
        network.forEachNode {
            it.ledArray.leds.forEach {
                it.color.fade(ColorMode.color(0), 0.05f)
            }
        }
    }

    override fun update() {
    }

    override fun stop() {
    }

    override fun dispose() {
    }
}