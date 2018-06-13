package ch.bildspur.anna.mapping

import ch.bildspur.anna.util.ColorMode
import processing.core.PImage

abstract class Fixture {
    var color = ColorMode.color(0)

    abstract fun updateColor(image : PImage)

    fun PImage.readPixel(x : Int, y : Int) : Int
    {
        val cx = Math.min(this.width, Math.max(0, x))
        val cy = Math.min(this.height, Math.max(0, y))

        return this.get(cx, cy)
    }
}