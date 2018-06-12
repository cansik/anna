package ch.bildspur.anna.util

import ch.bildspur.anna.model.ann.Network
import ch.bildspur.anna.model.ann.Neuron

fun Network.forEachNode(block : (Neuron) -> Unit)
{
    this.layers.forEach {layer ->
        layer.neurons.forEach {
            block(it)
        }
    }
}