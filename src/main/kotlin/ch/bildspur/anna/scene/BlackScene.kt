package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.util.forEachNode

class BlackScene(project : Project) : BaseScene(project) {
    private val task = TimerTask(1000, { update() })

    override val name: String
        get() = "Black"

    override val timerTask: TimerTask
        get() = task

    override fun start() {
        // set all led's one black
        network.forEachNode {
            it.ledArray.leds.forEach {
                it.color.fadeH(200f, 0.05f)
                it.color.fadeS(0f, 0.05f)
                it.color.fadeB(0f, 0.05f)
            }
        }

        super.start()
    }

    override fun update() {
    }
}