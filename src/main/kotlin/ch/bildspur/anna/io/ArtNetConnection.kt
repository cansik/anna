package ch.bildspur.anna.io

import ch.bildspur.artnet.ArtNet
import ch.bildspur.artnet.ArtNetException
import ch.bildspur.artnet.ArtNetNode
import ch.bildspur.artnet.packets.ArtDmxPacket
import java.net.InetAddress

class ArtNetConnection {
    private var sequenceId: Int = 0
    private var artnet = ArtNet()
    private var receiver: ArtNetNode? = null

    var running = false
        internal set

    fun open() {
        open(null)
    }

    fun open(address: String?) {
        try {
            artnet.start()
            setReceiver(address)
            running = true
        } catch (e: ArtNetException) {
            e.printStackTrace()
        }

    }

    fun setReceiver(address: String?) {
        if (null == address) {
            receiver = null
            return
        }

        receiver = createNode(address)
    }

    fun createNode(address: String): ArtNetNode? {
        try {
            val node = ArtNetNode()
            node.ipAddress = InetAddress.getByName(address)
            return node
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun close() {
        artnet.stop()
        running = false
    }

    fun send(universe: Int, data: ByteArray) {
        send(receiver, universe, data)
    }

    fun send(node: ArtNetNode?, universe: Int, data: ByteArray) {
        val dmx = ArtDmxPacket()

        dmx.setUniverse(0, universe)
        dmx.sequenceID = sequenceId++ % 256
        dmx.setDMX(data, data.size)

        if (node != null)
            artnet.unicastPacket(dmx, node)
        else
            artnet.broadcastPacket(dmx)
    }
}