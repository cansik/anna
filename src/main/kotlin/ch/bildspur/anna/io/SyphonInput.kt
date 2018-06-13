package ch.bildspur.anna.io

import processing.core.PApplet
import codeanticode.syphon.SyphonClient
import processing.core.PImage

class SyphonInput(parent : PApplet) : IOConnection(parent) {
    lateinit var client : SyphonClient

    @Volatile var isOpen = false

    lateinit var frame : PImage

    override fun setup() {
       frame = parent.createImage(0, 0, PApplet.RGB)
    }

    override fun open()
    {
        if(isOpen)
            return

        client = SyphonClient(parent)

        isOpen = true
    }

    override fun update()
    {
        if (client.newFrame()) {
            frame = client.getImage(frame, false)
        }
    }

    override fun close()
    {
        if(!isOpen)
            return

        //frame = null
        client.stop()

        isOpen = false
    }
}