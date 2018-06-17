package ch.bildspur.anna.model.config

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.view.properties.BooleanParameter
import ch.bildspur.anna.view.properties.SliderParameter
import com.google.gson.annotations.Expose

class Light {
    @Expose
    @BooleanParameter("ArtNet Rendering")
    var isArtNetRendering = DataModel(true)

    @Expose
    @SliderParameter("Luminosity", 0.0, 1.0, 0.01)
    var luminosity = DataModel(1f)

    @Expose
    @SliderParameter("Response", 0.0, 1.0, 0.01)
    var response = DataModel(0.5f)

    @Expose
    @SliderParameter("Trace", 0.0, 1.0, 0.01)
    var trace = DataModel(0f)
}