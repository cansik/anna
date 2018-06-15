package ch.bildspur.anna.model.config

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.util.ColorMode
import ch.bildspur.anna.view.properties.BooleanParameter
import ch.bildspur.anna.view.properties.FloatParameter
import ch.bildspur.anna.view.properties.IntParameter
import com.google.gson.annotations.Expose

class VisualisationSettings {
    // render mode
    @Expose
    @BooleanParameter("Render Nodes")
    var renderNodes = DataModel(true)

    @BooleanParameter("Render LEDs")
    @Expose
    var renderLEDs = DataModel(true)

    @BooleanParameter("Render Weights")
    @Expose
    var renderWeights = DataModel(true)

    @BooleanParameter("Render Information")
    @Expose
    var renderInformation = DataModel(false)

    // settings
    @FloatParameter("Layer Space")
    @Expose
    var layerSpace = DataModel(230f)

    @FloatParameter("Neuron Space")
    @Expose
    var neuronSpace = DataModel(100f)

    @FloatParameter("Stroke Thickness")
    @Expose
    var strokeThickness = DataModel(2f)

    @FloatParameter("Z-Height")
    @Expose
    var zHeight = DataModel(0f)

    @IntParameter("POF per Pixel")
    @Expose
    var pofPerPixel = DataModel(4)

    @FloatParameter("POF Spiral")
    @Expose
    var pofSpiral = DataModel(15f)

    // nodes
    @IntParameter("Node Color")
    @Expose
    var nodeColor = DataModel(ColorMode.color(255))

    @FloatParameter("Node Size")
    @Expose
    var nodeSize = DataModel(50f)

    @FloatParameter("Node Stroke Weight")
    @Expose
    var nodeStrokeWeight = DataModel(2f)

    @IntParameter("Node Detail")
    @Expose
    var nodeDetail = DataModel(5)

    // weights
    @FloatParameter("Weight Stroke Weight")
    @Expose
    var weightStrokeWeight = DataModel(1f)

    // led settings
    @FloatParameter("Led Space")
    @Expose
    var ledSpace = DataModel(8f)

    @FloatParameter("Led Size")
    @Expose
    var ledSize = DataModel(5f)

    // information
    @FloatParameter("Text Z-Height")
    @Expose
    var textZHeight = DataModel(3f)

    @FloatParameter("Layer Text Size")
    @Expose
    var layerTextSize = DataModel(20f)

    @FloatParameter("Layer Text Shift")
    @Expose
    var layerTextShift = DataModel(-200f)

    @FloatParameter("Neuron Text Size")
    @Expose
    var neuronTextSize = DataModel(16f)

    @FloatParameter("Neuron Text Shift")
    @Expose
    var neuronTextShift = DataModel(50f)

    @FloatParameter("LED Text Size")
    @Expose
    var ledTextSize = DataModel(10f)

    @FloatParameter("LED Text Shift")
    @Expose
    var ledTextShift = DataModel(20f)
}