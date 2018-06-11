package ch.bildspur.anna.renderer

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import processing.core.PGraphics
import processing.core.PVector
import processing.core.PApplet.radians




class SceneRenderer(val project: Project, val g: PGraphics) : IRenderer {
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

    override fun setup() {
        // setup dimensions
        annWidth = layers.map { (it.neurons.size - 1) * viewSettings.neuronSpace.value }.max() ?: 0f
        annHeight = (network.layers.size - 1) * viewSettings.layerSpace.value

        // setup nodes
        nodes = mutableListOf()

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
                val maxLedLength = (neuron.leds.leds.size - 1) * viewSettings.ledSpace.value
                neuron.leds.leds.forEachIndexed { i, led ->
                    g.pushMatrix()

                    // move to led position
                    g.translate((i * viewSettings.ledSpace.value) - (maxLedLength / 2f), 0f, 0f)

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
}