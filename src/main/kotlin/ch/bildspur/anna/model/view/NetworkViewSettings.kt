package ch.bildspur.anna.model.view

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.util.ColorMode
import com.google.gson.annotations.Expose

class NetworkViewSettings {
    // settings
    @Expose
    var layerSpace = DataModel(230f)

    @Expose
    var neuronSpace = DataModel(100f)

    @Expose
    var strokeThickness = DataModel(2f)

    @Expose
    var zHeight = DataModel(0f)

    @Expose
    var pofPerPixel = DataModel(4)

    @Expose
    var pofSpiral = DataModel(15f)

    // nodes
    @Expose
    var nodeColor = DataModel(ColorMode.color(255))

    @Expose
    var nodeSize = DataModel(50f)

    @Expose
    var nodeStrokeWeight = DataModel(2f)

    @Expose
    var nodeDetail = DataModel(5)

    // weights
    @Expose
    var weightStrokeWeight = DataModel(1f)
}