package ch.bildspur.anna.model.config

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.view.properties.StringParameter
import com.google.gson.annotations.Expose

class SceneSettings {

    @Expose
    @StringParameter("Active Scene")
    var activeScene = DataModel("")
}