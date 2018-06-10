package ch.bildspur.anna.view.properties

import ch.bildspur.anna.model.DataModel
import javafx.scene.control.CheckBox
import java.lang.reflect.Field

class BooleanProperty(field: Field, obj: Any, val annoation: BooleanParameter) : BaseProperty(field, obj) {

    val checkBox = CheckBox()

    init {
        children.add(checkBox)

        val model = field.get(obj) as DataModel<Boolean>
        model.onChanged += {
            checkBox.isSelected = model.value
        }
        model.fireLatest()

        checkBox.setOnAction {
            model.value = checkBox.isSelected
            propertyChanged(this)
        }
    }
}