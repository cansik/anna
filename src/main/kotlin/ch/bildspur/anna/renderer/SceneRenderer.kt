package ch.bildspur.anna.renderer

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.ann.Neuron
import processing.core.PGraphics
import processing.core.PVector
import processing.core.PApplet.radians


class SceneRenderer(project: Project, val g: PGraphics) : IRenderer {
    private val task = TimerTask(0, { render() }, "SceneRenderer")
    override val timerTask: TimerTask
        get() = task

    private val network = project.network
    private val layers = project.network.layers
    private val viewSettings = project.networkViewSettings

    // view variables
    private var annWidth: Float = 0f
    private var annHeight: Float = 0f

    private lateinit var nodes: MutableList<MutableList<PVector>>
    private lateinit var indexByNodes : MutableMap<Neuron, Pair<Int, Int>>

    override fun setup() {
        // setup dimensions
        annWidth = layers.map { (it.neurons.size - 1) * viewSettings.neuronSpace.value }.max() ?: 0f
        annHeight = (network.layers.size - 1) * viewSettings.layerSpace.value

        // setup nodes lookup tables
        nodes = mutableListOf()
        indexByNodes = HashMap()

        // init layer positions
        for (l in 0 until network.layers.size) {
            val layerSize = network.layers[l].neurons.size
            val nodeSpace = viewSettings.layerSpace.value
            val mx = nodeSpace * (layerSize - 1)

            // init nodes
            val layerPositions = mutableListOf<PVector>()
            nodes.add(layerPositions)

            // init node positions
            for (n in 0 until layerSize) {
                // calculate x y position
                val x = n * nodeSpace - mx / 2f
                val y = l * viewSettings.layerSpace.value - annHeight / 2f
                val z = viewSettings.zHeight.value

                layerPositions.add(PVector(x, y, z))
                indexByNodes[layers[l].neurons[n]] = Pair(l, n)
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
                val p = nodes[l][n]

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
                val p = nodes[l][n]

                g.pushMatrix()
                g.translate(p.x, p.y, p.z)

                // render led
                neuron.leds.leds.forEachIndexed { i, led ->
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
            g.stroke(255f)

            g.line(led1Pos.x, led1Pos.y, led1Pos.z, led2Pos.x, led2Pos.y, led2Pos.z)
        }
    }

    private fun renderWeightsOld() {
        // render weights
        for (l in 0 until network.layers.size - 1) {
            val layerSize = network.layers[l].neurons.size
            val nextLayerSize = network.layers[l + 1].neurons.size

            // render weights
            for (n in 0 until layerSize) {
                val pc = nodes[l][n]

                for (i in 0 until nextLayerSize) {
                    val pn = nodes[l + 1][i]

                    // render line from pc to pn
                    g.noFill()
                    g.strokeWeight(viewSettings.weightStrokeWeight.value)
                    g.stroke(255f)

                    //g.line(pc.x, pc.y, pc.z, pn.x, pn.y, pn.z);

                    // new string rendering
                    for (p in 0 until viewSettings.pofPerPixel.value) {
                        val c = PVector.lerp(pc, pn, 0.5f)
                        val pm = PVector()

                        // rotate c
                        val theta = 360f / viewSettings.pofPerPixel.value * p

                        // calculate new c position
                        g.pushMatrix()
                        g.translate(c.x, c.y, c.z)
                        g.rotateY(radians(theta))
                        g.translate(0f, 0f, viewSettings.pofSpiral.value)
                        pm.x = g.modelX(0f, 0f, 0f)
                        pm.y = g.modelY(0f, 0f, 0f)
                        pm.z = g.modelZ(0f, 0f, 0f)
                        g.popMatrix()

                        val pmf = PVector.lerp(pc, pm, 0.5f)
                        val pml = PVector.lerp(pm, pn, 0.5f)

                        g.beginShape()
                        g.vertex(pc.x, pc.y, pc.z)
                        g.curveVertex(pc.x, pc.y, pc.z)
                        g.curveVertex(pmf.x, pmf.y, pmf.z)
                        g.curveVertex(pm.x, pm.y, pm.z)
                        g.curveVertex(pml.x, pml.y, pml.z)
                        g.curveVertex(pn.x, pn.y, pn.z)
                        g.vertex(pn.x, pn.y, pn.z)
                        g.endShape()
                    }
                }
            }
        }
    }

    private fun getLEDPosition(neuron : Neuron, ledIndex : Int) : PVector
    {
        val position = getNeuronPosition(neuron)
        val shift = getLEDPositionYShift(neuron, ledIndex)

        return PVector(position.x, position.y + shift, position.z)
    }

    private fun getLEDPositionYShift(neuron : Neuron, ledIndex : Int) : Float
    {
        val maxLedLength = (neuron.leds.leds.size - 1) * viewSettings.ledSpace.value
        return (ledIndex * viewSettings.ledSpace.value) - (maxLedLength / 2f)
    }

    private fun getNeuronPosition(neuron : Neuron) : PVector
    {
        if(!indexByNodes.containsKey(neuron))
            return PVector()

        val indexes = indexByNodes[neuron]!!
        return nodes[indexes.first][indexes.second]
    }
}