package ch.bildspur.anna.mapping

import ch.bildspur.anna.util.ColorMode
import processing.core.PImage

class RectangleFixture(val x : Int, val y : Int, val width : Int, val height : Int) : Fixture() {

    override fun updateColor(image: PImage) {
        // aggregation variables
        var h = 0f
        var s = 0f
        var b = 0f

        // check if there are pixels to read
        if( width * height <= 0)
            return

        var pixelCount = 0

        // read pixels
        (0 until width).forEach { ix ->
            (0 until height).forEach { iy ->
                val px = ix + x
                val py = iy + y

                // read pixel
                val pixel = image.readPixel(px, py)

                if(pixel != ColorMode.color(0)) {
                    pixelCount++

                    h += ColorMode.hue(pixel)
                    s += ColorMode.saturation(pixel)
                    b += ColorMode.brightness(pixel)
                }
            }
        }

        color = ColorMode.color(h / pixelCount, s / pixelCount, b / pixelCount)
    }
}