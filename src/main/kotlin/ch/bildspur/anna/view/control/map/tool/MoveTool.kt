package ch.bildspur.anna.view.control.map.tool

import ch.bildspur.anna.event.Event
import ch.bildspur.anna.view.control.map.MiniMap
import ch.bildspur.anna.view.control.map.shape.NeuronShape
import ch.fhnw.afpars.ui.control.editor.shapes.BaseShape
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent

class MoveTool : BaseEditorTool() {
    val shapesSelected = Event<List<BaseShape>>()

    override val cursor: Cursor
        get() = Cursor.DEFAULT

    var shapes: List<NeuronShape> = emptyList()

    var dragStart = Point2D.ZERO!!

    override fun onCanvasMouseMoved(miniMap: MiniMap, event: MouseEvent) {
        val point = Point2D(event.x, event.y)
        val shapes = miniMap.activeLayer.shapes.filter { it.visible }.filter { it.contains(point) }

        if (shapes.isNotEmpty())
            miniMap.cursor = Cursor.HAND
        else
            miniMap.cursor = cursor
    }

    override fun onCanvasMousePressed(miniMap: MiniMap, event: MouseEvent) {
        if (event.clickCount == 2) {
            // add new ledArray
            return
        }

        dragStart = Point2D(event.x, event.y)
        shapes = miniMap.activeLayer.shapes
                .filterIsInstance<NeuronShape>()
                .filter { it.visible }
                .filter { it.contains(dragStart) }

        shapes.forEach { it.marked = true }
        miniMap.redraw()
    }

    override fun onCanvasMouseDragged(miniMap: MiniMap, event: MouseEvent) {
        // drag
        val point = Point2D(event.x, event.y)
        val delta = point.subtract(dragStart)

        shapes.forEach {
            // update shape location
            it.location = Point2D(it.location.x + delta.x, it.location.y + delta.y)
            it.updateLocation()
        }

        dragStart = point
        miniMap.redraw()
    }

    override fun onCanvasMouseReleased(miniMap: MiniMap, event: MouseEvent) {
        shapes.forEach { it.marked = false }

        dragStart = Point2D.ZERO!!

        miniMap.redraw()
    }

    override fun onCanvasMouseClicked(miniMap: MiniMap, event: MouseEvent) {
        // check if items selected
        val point = Point2D(event.x, event.y)
        val shapes = miniMap.activeLayer.shapes.filter { it.visible }.filter { it.contains(point) }

        if (shapes.isNotEmpty())
            shapesSelected(shapes)
    }
}