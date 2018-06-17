package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.util.ColorMode
import ch.bildspur.anna.util.forEachNode

class BlinkScene(project : Project) : BaseScene(project) {
    private val task = TimerTask(3000, { update() })

    override val name: String
        get() = "Blink"

    override val timerTask: TimerTask
        get() = task

    var mode = 0

    override fun start() {
        // set all led's one black
        network.forEachNode {
            it.ledArray.leds.forEach {
                it.color.fadeB(0f, 0.05f)
            }
        }

        super.start()
    }

    override fun update() {
        // off
        if(mode == 0)
        {
            network.forEachNode {
                it.ledArray.leds.forEach {
                    it.color.fadeB(0f, 0.05f)
                }
            }
        }

        // red
        if(mode == 1)
        {
            network.forEachNode {
                it.ledArray.leds.forEach {
                    it.color.fade(ColorMode.color(0f, 100f, 100f), 0.05f)
                }
            }
        }

        // blue
        if(mode == 2)
        {
            network.forEachNode {
                it.ledArray.leds.forEach {
                    it.color.fade(ColorMode.color(120f, 100f, 100f), 0.05f)
                }
            }
        }

        // green
        if(mode == 3)
        {
            network.forEachNode {
                it.ledArray.leds.forEach {
                    it.color.fade(ColorMode.color(220f, 100f, 100f), 0.05f)
                }
            }
        }

        mode = (1 + mode) % 4
    }
}