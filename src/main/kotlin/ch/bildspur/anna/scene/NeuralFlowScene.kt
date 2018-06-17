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

    private val task = TimerTask(100, { update() })

    private val sceneSettings = project.sceneSettings

    override val name: String
        get() = "Neural Flow"

    override val timerTask: TimerTask
        get() = task

    val brainWaves = mutableListOf<BrainWave>()

    data class BrainWave(var currentLayer : Int,
                         var currentNode : Int,
                         var targetLayer : Int,
                         var targetNode : Int,
                         var speed : Float,
                         var distance : Float = 0f,
                         var dead : Boolean = false)

    override fun start() {
        // set all led's one black
        network.forEachNode {
            it.ledArray.leds.forEach {
                it.color.fadeH(200f, 0.05f)
                it.color.fadeS(100f, 0.05f)
                it.color.fadeB(100f, 0.05f)
            }
        }

        super.start()
    }

    override fun update() {
        // move waves
        brainWaves.forEach {
            // add speed
            it.distance += it.speed

            if(!it.dead && it.distance > 1.0)
            {
                // decide
                it.currentLayer = it.targetLayer
                it.currentNode = it.targetNode

                if(it.currentLayer < network.layers.size - 1)
                {
                    // select next layer and random neuron
                    it.targetLayer++
                    it.targetNode = rnd.randomInt(0, network.layers[it.targetLayer].neurons.size - 1)
                }
                else
                {
                    it.dead = true
                }
            }
        }

        // remove dead
        brainWaves.removeAll { it.dead }

        // add new random movers
        if(rnd.randomBoolean(sceneSettings.neuronRandomAddFactor.value))
        {
            brainWaves.add(BrainWave(
                    0,
                    rnd.randomInt(0, network.layers[0].neurons.size - 1),
                    1,
                    rnd.randomInt(0, network.layers[1].neurons.size - 1),
                    sceneSettings.neuronSpeed.value
            ))
        }

        // turn all off
        network.weights.forEach {
            it.led1.color.fadeB(0f, sceneSettings.neuronFadeOffSpeed.value)
            it.led2.color.fadeB(0f, sceneSettings.neuronFadeOffSpeed.value)
        }

        // send color
        brainWaves.forEach {
            // find weight
            val weight = network.weights.find { weight ->
                weight.layerIndex1.value == it.currentLayer &&
                weight.layerIndex2.value == it.targetLayer &&
                weight.neuronIndex1.value == it.currentNode &&
                weight.neuronIndex2.value == it.targetNode
            }

            if(weight != null)
            {
                weight.led1.color.fadeB(100f, sceneSettings.neuronFadeOnSpeed.value)
                weight.led2.color.fadeB(100f, sceneSettings.neuronFadeOnSpeed.value)
            }
        }
    }
}