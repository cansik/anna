package ch.bildspur.anna.renderer

import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import processing.core.PGraphics
import processing.core.PVector


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
        // render nodes
        layers.forEachIndexed { l, layer ->
            layer.neurons.forEachIndexed { n, neuron ->
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

    override fun dispose() {
    }
}