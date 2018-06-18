package ch.bildspur.anna.model.config

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.view.properties.FloatParameter
import ch.bildspur.anna.view.properties.SliderParameter
import ch.bildspur.anna.view.properties.StringParameter
import com.google.gson.annotations.Expose

class SceneSettings {

    @Expose
    @StringParameter("Active Scene", isEditable = false)
    var activeScene = DataModel("")

    @Expose
    @SliderParameter("Stars Rnd On Factor", 0.8, 1.0, 0.01)
    var starsRandomOnFactor = DataModel(0.95f)

    @Expose
    @SliderParameter("Stars Rnd Off Factor", 0.8, 1.0, 0.01)
    var starsRandomOffFactor = DataModel(0.9f)

    @Expose
    @SliderParameter("Stars Fade Off Speed", 0.01, 1.0, 0.01)
    var starsFadeOffSpeed = DataModel(0.05f)

    @Expose
    @SliderParameter("Stars Rnd On Speed", 0.01, 1.0, 0.01)
    var starsFadeOnSpeed = DataModel(0.05f)

    // neurons

    @Expose
    @SliderParameter("Neuron Rnd Add Factor", 0.8, 1.0, 0.01)
    var neuronRandomAddFactor = DataModel(0.95f)

    @Expose
    @SliderParameter("Neuron Fade On Speed", 0.01, 1.0, 0.01)
    var neuronFadeOnSpeed = DataModel(0.5f)

    @Expose
    @SliderParameter("Nueron Rnd Off Speed", 0.01, 1.0, 0.01)
    var neuronFadeOffSpeed = DataModel(0.5f)

    @Expose
    @SliderParameter("Nueron Speed", 0.01, 0.5, 0.01)
    var neuronSpeed = DataModel(0.05f)
}