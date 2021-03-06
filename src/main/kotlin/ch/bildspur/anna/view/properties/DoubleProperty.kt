package ch.bildspur.anna.view.properties

import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.view.control.RelationNumberField
import javafx.scene.control.TextFormatter
import javafx.util.converter.DoubleStringConverter
import java.lang.reflect.Field

class DoubleProperty(field: Field, obj: Any, val annotation: DoubleParameter) : BaseProperty(field, obj) {
    val numberField = RelationNumberField<Double>(TextFormatter(DoubleStringConverter()))

    init {
        children.add(numberField)
        numberField.setValue(10.0)

        // setup two way binding
        val model = field.get(obj) as DataModel<Double>
        model.onChanged += {
            numberField.setValue(model.value)
        }
        model.fireLatest()

        numberField.setOnAction {
            model.value = numberField.getValue()
            propertyChanged(this)
        }
    }
}