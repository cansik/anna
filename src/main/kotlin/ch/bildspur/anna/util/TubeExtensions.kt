package ch.bildspur.anna.util

import ch.bildspur.anna.model.light.Led
import ch.bildspur.anna.model.light.LedArray

fun List<LedArray>.forEachLED(block: (led: Led) -> Unit) {
    this.flatMap { it.leds }.forEach {
        block(it)
    }
}