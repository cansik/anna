package ch.bildspur.anna.mapping

import ch.bildspur.anna.util.ColorMode
import processing.core.PApplet
import processing.core.PImage
import processing.core.PVector

class IntensityLineFixture(val p1 : PVector,
                           val p2 : PVector,
                           val thickness : Int = 20,
                           val minIntensity : Float = 0.1f,
                           val maxIntensity : Float = 1.0f) : Fixture() {

    val subFixtures = mutableListOf<RectangleFixture>()

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

        if(subFixtures.size <= 0)
            return

        var pixelCount = 0
        var totalIntensity = 0f

        subFixtures.forEachIndexed { i, f ->
            f.updateColor(image)

            val pixel = f.color

            if(pixel != ColorMode.color(0)) {
                pixelCount++

                val intensity = PApplet.map(i.toFloat(), 0f, subFixtures.size.toFloat(), maxIntensity, minIntensity)
                totalIntensity += intensity

                h += ColorMode.hue(pixel) * intensity
                s += ColorMode.saturation(pixel) * intensity
                b += ColorMode.brightness(pixel) * intensity
            }
        }

        color = ColorMode.color(h / totalIntensity, s / totalIntensity, b / totalIntensity)
    }
}