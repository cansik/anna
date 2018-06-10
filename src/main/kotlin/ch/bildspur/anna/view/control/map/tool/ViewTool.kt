package ch.bildspur.anna.view.control.map.tool

import ch.bildspur.anna.event.Event
import ch.bildspur.anna.view.control.map.MiniMap
import ch.fhnw.afpars.ui.control.editor.shapes.BaseShape
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 25.01.17.
 */
class ViewTool : BaseEditorTool() {
    val scaleSpeed = 1.0 / 50.0

    var dragStart = Point2D.ZERO!!

    val shapesSelected = Event<List<BaseShape>>()

    override val cursor: Cursor
        get() = Cursor.OPEN_HAND

    override fun onEditorMousePressed(miniMap: MiniMap, event: MouseEvent) {
        if (event.clickCount == 2) {
            miniMap.resetZoom()
            return
        }

        dragStart = Point2D(event.x, event.y)
    }

    override fun onEditorMouseDragged(miniMap: MiniMap, event: MouseEvent) {
        // drag
        val point = Point2D(event.x, event.y)
        val delta = dragStart.subtract(point)

        miniMap.canvasTransformation = delta.multiply(-1.0)
        dragStart = point

        miniMap.resize()
    }

    override fun onCanvasMouseClicked(miniMap: MiniMap, event: MouseEvent) {
        // check if items selected
        val point = Point2D(event.x, event.y)
        val shapes = miniMap.layers.flatMap { it.shapes.filter { it.visible }.filter { it.contains(point) } }

        if (shapes.isNotEmpty())
            shapesSelected(shapes)
    }

    override fun onEditorScroll(miniMap: MiniMap, event: ScrollEvent) {
        // zoom point
        miniMap.zoomTransformation = Point2D(event.x, event.y)

        // scale
        miniMap.zoomScale += -1 * event.deltaY * scaleSpeed
        miniMap.zoomScale = Math.min(Math.max(miniMap.minimumZoom, miniMap.zoomScale), miniMap.maximumZoom)
        miniMap.resize()
    }
}