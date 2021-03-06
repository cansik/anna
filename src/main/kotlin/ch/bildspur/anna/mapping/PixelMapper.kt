package ch.bildspur.anna.mapping

import processing.core.PImage

class PixelMapper(val fixtures : MutableList<Fixture> = mutableListOf()) {

    fun updateFixtures(image : PImage)
    {
        fixtures.forEach { it.updateColor(image) }
    }
}