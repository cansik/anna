package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.ann.Network
import ch.bildspur.anna.util.ColorMode
import ch.bildspur.anna.util.ExtendedRandom
import ch.bildspur.anna.util.forEachNode

class StarPatternScene(network: Network) : BaseScene(network) {
    private var randomOnFactor = 0.95f
    private var randomOffFactor = 0.8f
    private var fadeSpeed = 0.01f

    private val rnd = ExtendedRandom()

    private val task = TimerTask(500, { update() })

    override val name: String
        get() = "StarPattern Scene"

    override val timerTask: TimerTask
        get() = task


    override fun setup() {
        // set all led's one black
        network.forEachNode {
            it.ledArray.leds.forEach {
                it.color.fade(ColorMode.color(0, 0, 100), 0.05f)
            }
        }
    }

    override fun update() {
        network.forEachNode {
            it.ledArray.leds.forEach {
                val ledBrightness = ColorMode.brightness(it.color.color)

                if (ledBrightness > 10) {
                    //led is ON
                    if (rnd.randomBoolean(randomOffFactor)) {
                        it.color.fadeB(0f, fadeSpeed)
                    }
                } else {
                    //led is OFF
                    if (rnd.randomBoolean(randomOnFactor)) {
                        it.color.fadeB(rnd.randomFloat(50f, 100f), fadeSpeed)
                    }
                }
            }
        }
    }

    override fun stop() {
    }

    override fun dispose() {
    }
}