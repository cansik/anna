package ch.bildspur.anna.io

import processing.core.PApplet

abstract class IOConnection(val parent : PApplet) {

    @Volatile var isOpen = false

    abstract fun setup()

    open fun open()
    {
        if(isOpen)
            return

        isOpen = true
    }

    abstract fun update()

    open fun close()
    {
        if(!isOpen)
            return

        isOpen = false
    }

}