package ch.bildspur.anna.model.config

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.view.properties.BooleanParameter
import com.google.gson.annotations.Expose

class SyphonSettings {
    @Expose
    @BooleanParameter("Show Syphon Input")
    var showSyphonInput = DataModel(false)

    @Expose
    @BooleanParameter("Async Input")
    var asyncSyphonInput = DataModel(false)
}