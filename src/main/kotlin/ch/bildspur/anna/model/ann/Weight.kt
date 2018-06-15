package ch.bildspur.anna.model.ann

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.model.light.Led
import ch.bildspur.anna.view.properties.IntParameter
import ch.bildspur.anna.view.properties.StringParameter
import com.google.gson.annotations.Expose

class Weight(@Expose
             @IntParameter("Layer 1")
             val layerIndex1: DataModel<Int>,
             @Expose
             @IntParameter("Neuron 1")
             val neuronIndex1: DataModel<Int>,
             @Expose
             @IntParameter("LED 1")
             val ledIndex1: DataModel<Int>,
             @Expose
             @IntParameter("Layer 2")
             val layerIndex2: DataModel<Int>,
             @Expose
             @IntParameter("Neuron 2")
             val neuronIndex2: DataModel<Int>,
             @Expose
             @IntParameter("LED 2")
             val ledIndex2: DataModel<Int>,
             var network : Network = Network()) {

    constructor(layerIndex1: Int,
                neuronIndex1: Int,
                ledIndex1: Int,
                layerIndex2: Int,
                neuronIndex2: Int,
                ledIndex2: Int,
                network : Network = Network()) : this(
            DataModel(layerIndex1),
            DataModel(neuronIndex1),
            DataModel(ledIndex1),
            DataModel(layerIndex2),
            DataModel(neuronIndex2),
            DataModel(ledIndex2),
            network)

    val neuron1: Neuron
        get() = network.layers[layerIndex1.value].neurons[neuronIndex1.value]

    val neuron2: Neuron
        get() = network.layers[layerIndex2.value].neurons[neuronIndex2.value]

    val led1: Led
        get() = neuron1.ledArray[ledIndex1.value]

    val led2: Led
        get() = neuron2.ledArray[ledIndex2.value]

    fun isConnected(neuron : Neuron) : Boolean
    {
        return neuron1 == neuron || neuron2 == neuron
    }

    fun isConnected(led : Led) : Boolean
    {
        return led == led1 || led == led2
    }
}