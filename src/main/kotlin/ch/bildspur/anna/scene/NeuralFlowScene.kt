package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.ann.Network
import ch.bildspur.anna.util.ColorMode
import ch.bildspur.anna.util.ExtendedRandom
import ch.bildspur.anna.util.forEachNode
import processing.core.PVector

class NeuralFlowScene(project : Project) : BaseScene(project) {
    private val rnd = ExtendedRandom()

    private val task = TimerTask(500, { update() })

    override val name: String
        get() = "Neural Flow"

    override val timerTask: TimerTask
        get() = task

    val brainWaves = mutableListOf<BrainWave>()

    data class BrainWave(var position : PVector)

    override fun start() {
        // set all led's one black
        network.forEachNode {
            it.ledArray.leds.forEach {
                it.color.fadeH(200f, 0.05f)
                it.color.fadeS(80f, 0.05f)
                it.color.fadeB(100f, 0.05f)
            }
        }

        super.start()
    }

    override fun update() {

    }
}