package ch.bildspur.anna.model

import ch.bildspur.anna.Sketch
import ch.bildspur.anna.model.ann.Network
import ch.bildspur.anna.model.config.Light
import ch.bildspur.anna.model.light.DmxNode
import ch.bildspur.anna.model.config.SceneSettings
import ch.bildspur.anna.model.config.SyphonSettings
import ch.bildspur.anna.model.config.VisualisationSettings
import ch.bildspur.anna.view.properties.BooleanParameter
import ch.bildspur.anna.view.properties.IntParameter
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
    var highResMode = DataModel(false)

    @Expose
    @BooleanParameter("High FPS Mode*")
    var highFPSMode = DataModel(true)

    @Expose
    @BooleanParameter("Enable VSYNC*")
    var vsyncMode = DataModel(true)

    @Expose
    @BooleanParameter("Fullscreen Mode*")
    var isFullScreenMode = DataModel(false)

    @Expose
    @IntParameter("Fullscreen Display*")
    var fullScreenDisplay = DataModel(0)

    @Expose
    @BooleanParameter("ArtNet Rendering")
    var isArtNetRendering = DataModel(true)

    @Expose
    var nodes = CopyOnWriteArrayList<DmxNode>()

    @Expose
    var network = Network()

    @Expose
    var visualisationSettings = VisualisationSettings()

    @Expose
    var sceneSettings = SceneSettings()

    @Expose
    var light = Light()

    @Expose
    var syphonSettings = SyphonSettings()
}