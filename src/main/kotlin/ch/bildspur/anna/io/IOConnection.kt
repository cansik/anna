package ch.bildspur.anna.io

import processing.core.PApplet

abstract class IOConnection(val parent : PApplet) {

    abstract fun setup()

    abstract fun open()

    abstract fun update()

    abstract fun close()

}