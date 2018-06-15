package ch.bildspur.anna.view.util

import ch.bildspur.anna.model.ann.Weight
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.util.Callback

fun TreeView<TagItem>.items(current: TreeItem<TagItem> = this.root,
                            items: MutableList<TreeItem<TagItem>> = mutableListOf<TreeItem<TagItem>>())
        : MutableList<TreeItem<TagItem>> {
    items.add(current)

    current.children.forEach {
        this.items(it, items)
    }

    return items
}

fun <T> TableColumn<Weight, T>.cellFactory(field: (Weight) -> T) {
    this.cellValueFactory = Callback<TableColumn.CellDataFeatures<Weight, T>, ObservableValue<T>> { p0 -> ReadOnlyObjectWrapper<T>(field(p0?.value!!)) }
}