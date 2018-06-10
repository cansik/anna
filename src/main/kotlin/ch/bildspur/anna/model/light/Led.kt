package ch.bildspur.anna.model.light

import ch.bildspur.anna.model.FadeColor


class Led(var address: Int, color: Int) {
    companion object {
        @JvmStatic
        val LED_ADDRESS_SIZE = 3
    }

    var color: FadeColor = FadeColor(color)
}