package ch.bildspur.anna.model

import ch.bildspur.anna.Sketch
import ch.bildspur.anna.model.ann.Network
import ch.bildspur.anna.model.light.DmxNode
import ch.bildspur.anna.model.light.LedArray
import ch.bildspur.anna.model.view.NetworkViewSettings
import ch.bildspur.anna.view.properties.BooleanParameter
import ch.bildspur.anna.view.properties.IntParameter
import ch.bildspur.anna.view.properties.SliderParameter
import ch.bildspur.anna.view.properties.StringParameter
import com.google.gson.annotations.Expose
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by cansik on 11.07.17.
 */
class Project {
    @Expose
    @StringParameter("Name")
    var name = DataModel("${Sketch.NAME} Project")

    @Expose
    @BooleanParameter("High Res Mode*")
    var highResMode = DataModel(true)

    @Expose
    @BooleanParameter("High FPS Mode*")
    var highFPSMode = DataModel(true)

    @Expose
    @BooleanParameter("High Detail Mode")
    var highDetail = DataModel(true)

    @Expose
    @BooleanParameter("Fullscreen Mode*")
    var isFullScreenMode = DataModel(false)

    @Expose
    @IntParameter("Fullscreen Display*")
    var fullScreenDisplay = DataModel(0)

    @Expose
    @SliderParameter("LedArray Detail", 2.0, 10.0, 1.0)
    var tubeDetail = DataModel(5.0)

    @Expose
    @BooleanParameter("ArtNet Rendering")
    var isArtNetRendering = DataModel(true)


    @Expose
    var nodes = CopyOnWriteArrayList<DmxNode>()

    @Expose
    var network = Network()

    @Expose
    var networkViewSettings = NetworkViewSettings()

    @Expose
    var light = Light()
}