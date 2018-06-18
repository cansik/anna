package ch.bildspur.anna.model.ann

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.model.light.Led
import ch.bildspur.anna.util.ColorMode
import ch.bildspur.anna.view.properties.ActionParameter
import ch.bildspur.anna.view.properties.IntParameter
import ch.bildspur.anna.view.properties.StringParameter
import ch.bildspur.anna.view.properties.BooleanParameter
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

    // serialisation initializer
    constructor() : this(0, 0, 0,0,0, 0)

    private val fadeSpeed = 0.05f

    @Expose
    @BooleanParameter("POF Connected")
    var isPofConnected = DataModel(false)

    @Expose
    @StringParameter("MarkerColor")
    var markerColor = DataModel("")

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

    @ActionParameter("Mark LED 1", "Select")
    val markLED1 = {
        led1.color.fade(ColorMode.color(0, 0, 100), fadeSpeed)
    }

    @ActionParameter("Mark LED 2", "Select")
    val markLED2 = {
        led2.color.fade(ColorMode.color(0, 0, 100), fadeSpeed)
    }

    @ActionParameter("Mark Weight", "White")
    val markWeightWhite = {
        led1.color.fade(ColorMode.color(0, 0, 100), fadeSpeed)
        led2.color.fade(ColorMode.color(0, 0, 100), fadeSpeed)

        markerColor.value = "White"
    }

    @ActionParameter("Mark Weight", "Red")
    val markWeightRed = {
        led1.color.fade(ColorMode.color(0, 100, 100), fadeSpeed)
        led2.color.fade(ColorMode.color(0, 100, 100), fadeSpeed)

        markerColor.value = "Red"
    }

    @ActionParameter("Mark Weight", "Green")
    val markWeightGreen = {
        led1.color.fade(ColorMode.color(120, 100, 100), fadeSpeed)
        led2.color.fade(ColorMode.color(120, 100, 100), fadeSpeed)

        markerColor.value = "Green"
    }

    @ActionParameter("Mark Weight", "Blue")
    val markWeightBlue = {
        led1.color.fade(ColorMode.color(244, 100, 100), fadeSpeed)
        led2.color.fade(ColorMode.color(244, 100, 100), fadeSpeed)

        markerColor.value = "Blue"
    }

    @ActionParameter("Mark Weight", "Magenta")
    val markWeightMagenta = {
        led1.color.fade(ColorMode.color(310, 100, 100), fadeSpeed)
        led2.color.fade(ColorMode.color(310, 100, 100), fadeSpeed)

        markerColor.value = "Magenta"
    }

    @ActionParameter("Mark Weight", "Cyan")
    val markWeightCyan = {
        led1.color.fade(ColorMode.color(195, 100, 100), fadeSpeed)
        led2.color.fade(ColorMode.color(195, 100, 100), fadeSpeed)

        markerColor.value = "Cyan"
    }

    @ActionParameter("Mark Weight", "Yellow")
    val markWeightYellow = {
        led1.color.fade(ColorMode.color(50, 100, 100), fadeSpeed)
        led2.color.fade(ColorMode.color(50, 100, 100), fadeSpeed)
        markerColor.value = "Yellow"
    }

    @ActionParameter("Weight", "Blackout")
    val blackoutWeight = {
        led1.color.fade(ColorMode.color(0, 100, 0), fadeSpeed)
        led2.color.fade(ColorMode.color(0, 100, 0), fadeSpeed)

        markerColor.value = ""
    }

    override fun toString() : String
    {
        return ("Weight (${layerIndex1.value}.${neuronIndex1.value}.${ledIndex1.value} - ${layerIndex2.value}.${neuronIndex2.value}.${ledIndex2.value})")
    }
}