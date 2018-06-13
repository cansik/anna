package ch.bildspur.anna.renderer

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.ann.Neuron
import processing.core.PGraphics
import processing.core.PVector


class VisualisationRenderer(project: Project, val g: PGraphics) : IRenderer {
    private val task = TimerTask(0, { render() }, "VisualisationRenderer")
    override val timerTask: TimerTask
        get() = task

    private val network = project.network
    private val layers = project.network.layers
    private val viewSettings = project.networkViewSettings

    // view variables
    var annWidth: Float = 0f
    var annHeight: Float = 0f

    // lookup tables
    lateinit var neuronPositions: MutableList<MutableList<PVector>>
    lateinit var indexByNeurons : MutableMap<Neuron, Pair<Int, Int>>

    override fun setup() {
        // setup dimensions
        annWidth = layers.map { (it.neurons.size - 1) * viewSettings.neuronSpace.value }.max() ?: 0f
        annHeight = (network.layers.size - 1) * viewSettings.layerSpace.value

        // setup neuronPositions lookup tables
        neuronPositions = mutableListOf()
        indexByNeurons = HashMap()

        // init layer positions
        for (l in 0 until network.layers.size) {
            val layerSize = network.layers[l].neurons.size
            val nodeSpace = viewSettings.layerSpace.value
            val mx = nodeSpace * (layerSize - 1)

            // init neuronPositions
            val layerPositions = mutableListOf<PVector>()
            neuronPositions.add(layerPositions)

            // init node positions
            for (n in 0 until layerSize) {
                // calculate x y position
                val x = n * nodeSpace - mx / 2f
                val y = l * viewSettings.layerSpace.value - annHeight / 2f
                val z = viewSettings.zHeight.value

                layerPositions.add(PVector(x, y, z))
                indexByNeurons[layers[l].neurons[n]] = Pair(l, n)
            }
        }
    }

    override fun render() {
        if (viewSettings.renderNodes.value)
            renderNodes()

        if (viewSettings.renderLEDs.value)
            renderLEDs()

        if (viewSettings.renderWeights.value)
            renderWeights()
    }

    override fun dispose() {
    }

    private fun renderNodes() {
        layers.forEachIndexed { l, layer ->
            layer.neurons.forEachIndexed { n, _ ->
                val p = neuronPositions[l][n]

                g.pushMatrix()
                g.translate(p.x, p.y, p.z)

                g.sphereDetail(viewSettings.nodeDetail.value)
                g.noFill()
                g.stroke(viewSettings.nodeColor.value)
                g.strokeWeight(viewSettings.nodeStrokeWeight.value)

                g.sphere(viewSettings.nodeSize.value)

                g.popMatrix()
            }
        }
    }

    private fun renderLEDs() {
        layers.forEachIndexed { l, layer ->
            layer.neurons.forEachIndexed { n, neuron ->
                val p = neuronPositions[l][n]

                g.pushMatrix()
                g.translate(p.x, p.y, p.z)

                // render led
                neuron.ledArray.leds.forEachIndexed { i, led ->
                    g.pushMatrix()

                    // move to led position
                    val ledYPosition = getLEDPositionYShift(neuron, i)
                    g.translate(0f, ledYPosition, 0f)

                    // setup led view
                    g.noStroke()
                    g.fill(led.color.color)
                    g.box(viewSettings.ledSize.value)
                    g.popMatrix()
                }

                g.popMatrix()
            }
        }
    }

    private fun renderWeights() {
        network.weights.forEach {
            val led1Pos = getLEDPosition(it.neuron1, it.ledIndex1)
            val led2Pos = getLEDPosition(it.neuron2, it.ledIndex2)

            // render line from pc to pn
            g.noFill()
            g.strokeWeight(viewSettings.weightStrokeWeight.value)

            g.beginShape()
            g.stroke(it.led1.color.color)
            g.vertex(led1Pos.x, led1Pos.y, led1Pos.z)

            g.stroke(it.led2.color.color)
            g.vertex(led2Pos.x, led2Pos.y, led2Pos.z)
            g.vertex(led2Pos.x, led2Pos.y, led2Pos.z)
            g.endShape()
        }
    }

    fun getLEDPosition(neuron : Neuron, ledIndex : Int) : PVector
    {
        val position = getNeuronPosition(neuron)
        val shift = getLEDPositionYShift(neuron, ledIndex)

        return PVector(position.x, position.y + shift, position.z)
    }

    private fun getLEDPositionYShift(neuron : Neuron, ledIndex : Int) : Float
    {
        val maxLedLength = (neuron.ledArray.leds.size - 1) * viewSettings.ledSpace.value
        return (ledIndex * viewSettings.ledSpace.value) - (maxLedLength / 2f)
    }

    private fun getNeuronPosition(neuron : Neuron) : PVector
    {
        if(!indexByNeurons.containsKey(neuron))
            return PVector()

        val indexes = indexByNeurons[neuron]!!
        return neuronPositions[indexes.first][indexes.second]
    }
}