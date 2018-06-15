package ch.bildspur.anna.model.ann

import ch.bildspur.anna.model.light.Led
import com.google.gson.annotations.Expose

class Weight(@Expose val layerIndex1: Int,
             @Expose val neuronIndex1: Int,
             @Expose val ledIndex1: Int,
             @Expose val layerIndex2: Int,
             @Expose val neuronIndex2: Int,
             @Expose val ledIndex2: Int,
             var network : Network = Network()) {

    val neuron1: Neuron
        get() = network.layers[layerIndex1].neurons[neuronIndex1]

    val neuron2: Neuron
        get() = network.layers[layerIndex2].neurons[neuronIndex2]

    val led1: Led
        get() = neuron1.ledArray[ledIndex1]

    val led2: Led
        get() = neuron2.ledArray[ledIndex2]

    fun isConnected(neuron : Neuron) : Boolean
    {
        return neuron1 == neuron || neuron2 == neuron
    }

    fun isConnected(led : Led) : Boolean
    {
        return led == led1 || led == led2
    }
}