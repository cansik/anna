package ch.bildspur.anna.model.ann

import ch.bildspur.anna.model.light.Led
import com.google.gson.annotations.Expose

class Weight(@Expose val neuron1: Neuron,
             @Expose val ledIndex1: Int,
             @Expose val neuron2: Neuron,
             @Expose val ledIndex2: Int) {

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