package ch.bildspur.anna.view.properties

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.util.ColorMode
import javafx.scene.control.ColorPicker
import javafx.scene.paint.Color
import java.lang.reflect.Field
import kotlin.math.roundToInt

class ColorProperty(field: Field, obj: Any, val annoation: ColorParameter) : BaseProperty(field, obj) {

    val colorPicker = ColorPicker()

    init {
        children.add(colorPicker)

        val model = field.get(obj) as DataModel<Int>
        model.onChanged += {
            colorPicker.value = Color.hsb(
                    ColorMode.hue(model.value) / 360.0,
                    ColorMode.saturation(model.value) / 100.0,
                    ColorMode.brightness(model.value) / 100.0)
        }
        model.fireLatest()

        colorPicker.setOnAction {
            model.value = ColorMode.color(
                    (colorPicker.value.hue * 360.0).roundToInt(),
                    (colorPicker.value.saturation * 100.0).roundToInt(),
                    (colorPicker.value.brightness * 100.0).roundToInt())
            propertyChanged(this)
        }
    }
}