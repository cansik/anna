package ch.bildspur.anna.mapping

import ch.bildspur.anna.util.ColorMode
import processing.core.PApplet
import processing.core.PImage
import processing.core.PVector

class LineFixture(val p1 : PVector, val p2 : PVector, val thickness : Int = 20) : Fixture() {

    private val subFixtures = mutableListOf<Fixture>()

    init {
        setupSubFixtures()
    }

    private fun setupSubFixtures()
    {
        val distance = p1.dist(p2)
        val count = Math.round(distance / thickness)

        (0 until count).forEach {i ->
            val ni = i / count.toFloat()
            val pos = PVector.lerp(p1, p2, ni)

            // create fixture
            val fix = RectangleFixture(
                    Math.round(pos.x - (thickness / 2)),
                    Math.round(pos.y - (thickness / 2)),
                    thickness,
                    thickness)

            subFixtures.add(fix)
        }
    }

    override fun updateColor(image: PImage) {
        // aggregation variables
        var h = 0f
        var s = 0f
        var b = 0f

        val pixelCount = subFixtures.size

        if(pixelCount <= 0)
            return

        subFixtures.forEach {
            it.updateColor(image)

            val pixel = it.color

            h += ColorMode.hue(pixel)
            s += ColorMode.saturation(pixel)
            b += ColorMode.brightness(pixel)
        }

        color = ColorMode.color(h / pixelCount, s / pixelCount, b / pixelCount)
    }
}