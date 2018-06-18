package ch.bildspur.anna.view.properties

import ch.bildspur.anna.model.DataModel
import javafx.scene.control.CheckBox
import javafx.scene.control.ColorPicker
import java.lang.reflect.Field

class ColorProperty(field: Field, obj: Any, val annoation: ColorParameter) : BaseProperty(field, obj) {

    val colorPicker = ColorPicker()

    init {
        children.add(colorPicker)

        val model = field.get(obj) as DataModel<Int>
        model.onChanged += {
            //colorPicker.color
        }
        model.fireLatest()

        colorPicker.setOnAction {
            //model.value = colorPicker.isSelected
            propertyChanged(this)
        }
    }
}