package ch.bildspur.anna.renderer

import ch.bildspur.anna.io.ArtNetConnection
import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.light.LedArray
import ch.bildspur.anna.model.light.DmxUniverse
import ch.bildspur.artnet.ArtNetNode

class ArtNetRenderer(val project: Project, val artnet: ArtNetConnection) : IRenderer {
    lateinit var universesToNodes: Map<DmxUniverse, ArtNetNode>
    lateinit var indexToUniverses: Map<Int, DmxUniverse>

    lateinit var ledArrays: List<LedArray>

    private val task = TimerTask(0, { render() }, "ArtNetRenderer")
    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        buildUniverseIndex()

        // build led array index
        ledArrays = project.network.layers.flatMap { it.neurons.map { it.ledArray }}
    }

    override fun render() {
        // check if artnet rendering is used
        if (!project.light.isArtNetRendering.value)
            return

        ledArrays.groupBy { it.universe.value }.forEach {
            val universe = indexToUniverses[it.key]!!
            val node = universesToNodes[universe]!!

            val light = project.light
            universe.stageDmx(it.value, light.luminosity.value, light.response.value, light.trace.value)
            artnet.send(node, universe.id.value, universe.dmxData)
        }
    }

    fun buildUniverseIndex() {
        universesToNodes = project.nodes
                .flatMap { n -> n.universes.map { u -> Pair(u, n) } }
                .associate { it.first to artnet.createNode(it.second.address.value)!! }

        indexToUniverses = universesToNodes.keys.associate { it.id.value to it }
    }

    override fun dispose() {

    }
}