package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.ann.Network
import ch.bildspur.anna.util.ColorMode
import ch.bildspur.anna.util.ExtendedRandom
import ch.bildspur.anna.util.forEachNode

class StarPatternScene(project : Project) : BaseScene(project) {
    private val rnd = ExtendedRandom()

    private val sceneSettings = project.sceneSettings

    private val task = TimerTask(500, { update() })

    override val name: String
        get() = "Star Pattern"

    override val timerTask: TimerTask
        get() = task

    override fun start() {
        // set all leds on
        network.forEachNode {
            it.ledArray.leds.forEach {
                it.color.fade(ColorMode.color(200f, 0f, 100f), 0.05f)
            }
        }

        super.start()
    }

    override fun update() {
        network.forEachNode {
            it.ledArray.leds.forEach {
                val ledBrightness = ColorMode.brightness(it.color.color)

                if (ledBrightness > 10) {
                    //led is ON
                    if (rnd.randomBoolean(sceneSettings.starsFadeOffSpeed.value)) {
                        it.color.fadeB(0f, sceneSettings.starsFadeOnSpeed.value)
                    }
                } else {
                    //led is OFF
                    if (rnd.randomBoolean(sceneSettings.starsRandomOnFactor.value)) {
                        it.color.fadeB(rnd.randomFloat(50f, 100f), sceneSettings.starsFadeOffSpeed.value)
                    }
                }
            }
        }
    }
}