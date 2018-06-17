package ch.bildspur.anna.model.config

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.view.properties.FloatParameter
import ch.bildspur.anna.view.properties.SliderParameter
import ch.bildspur.anna.view.properties.StringParameter
import com.google.gson.annotations.Expose

class SceneSettings {

    @Expose
    @StringParameter("Active Scene")
    var activeScene = DataModel("")

    @Expose
    @SliderParameter("Stars Rnd On Factor", 0.8, 1.0, 0.01)
    var starsRandomOnFactor = DataModel(0.95f)

    @Expose
    @SliderParameter("Stars Rnd Off Factor", 0.8, 1.0, 0.01)
    var starsRandomOffFactor = DataModel(0.9f)

    @Expose
    @SliderParameter("Stars Fade On Speed", 0.01, 1.0, 0.01)
    var starsFadeOffSpeed = DataModel(0.05f)

    @Expose
    @SliderParameter("Stars Rnd Off Speed", 0.01, 1.0, 0.01)
    var starsFadeOnSpeed = DataModel(0.05f)
}