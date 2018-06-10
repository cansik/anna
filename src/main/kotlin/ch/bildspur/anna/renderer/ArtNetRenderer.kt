package ch.bildspur.anna.renderer

import artnet4j.ArtNetNode
import ch.bildspur.anna.artnet.ArtNetClient
import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.light.DmxNode
import ch.bildspur.anna.model.light.LedArray
import ch.bildspur.anna.model.light.DmxUniverse

class ArtNetRenderer(val project: Project, val artnet: ArtNetClient, val nodes: List<DmxNode>, val ledArrays: List<LedArray>) : IRenderer {
    lateinit var universesToNodes: Map<DmxUniverse, ArtNetNode>
    lateinit var indexToUniverses: Map<Int, DmxUniverse>

    private val task = TimerTask(15, { render() }, "ArtNetRenderer")
    override val timerTask: TimerTask
        get() = task

    override fun setup() {
        buildUniverseIndex()
    }

    override fun render() {
        // check if artnet rendering is used
        if (!project.isArtNetRendering.value)
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
        universesToNodes = nodes
                .flatMap { n -> n.universes.map { u -> Pair(u, n) } }
                .associate { it.first to artnet.createNode(it.second.address.value)!! }

        indexToUniverses = universesToNodes.keys.associate { it.id.value to it }
    }

    override fun dispose() {

    }
}