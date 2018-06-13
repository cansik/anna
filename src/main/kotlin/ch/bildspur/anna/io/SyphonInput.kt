package ch.bildspur.anna.io

import processing.core.PApplet
import codeanticode.syphon.SyphonClient
import processing.core.PImage
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class SyphonInput(parent: PApplet) : IOConnection(parent) {
    lateinit var client: SyphonClient

    val frame: PImage
        get() = if (buffer != null) buffer!! else blackFrame

    val frameLock = ReentrantLock()

    private var buffer: PImage? = null
    private lateinit var blackFrame: PImage

    override fun setup() {
        blackFrame = parent.createImage(0, 0, PApplet.RGB)
    }

    override fun open() {
        client = SyphonClient(parent)
    }

    override fun update() {
        if (client.newFrame()) {
            frameLock.withLock {
                buffer = client.getImage(buffer, true)
            }
        }
    }

    override fun close() {
        client.stop()
    }
}