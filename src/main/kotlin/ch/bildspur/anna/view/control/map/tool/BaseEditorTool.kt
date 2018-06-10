package ch.bildspur.anna.view.control.map.tool

import ch.bildspur.anna.view.control.map.MiniMap
import javafx.geometry.Point2D
import javafx.scene.Cursor
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 26.01.17.
 */
abstract class BaseEditorTool : IEditorTool {
    override fun onCanvasMouseClicked(miniMap: MiniMap, event: MouseEvent) {}
    override fun onCanvasMousePressed(miniMap: MiniMap, event: MouseEvent) {}
    override fun onCanvasMouseReleased(miniMap: MiniMap, event: MouseEvent) {}
    override fun onCanvasMouseDragged(miniMap: MiniMap, event: MouseEvent) {}
    override fun onCanvasMouseMoved(miniMap: MiniMap, event: MouseEvent) {}
    override fun onCanvasScroll(miniMap: MiniMap, event: ScrollEvent) {}
    override fun onCanvasKeyPressed(miniMap: MiniMap, event: KeyEvent) {}

    override fun onEditorMouseClicked(miniMap: MiniMap, event: MouseEvent) {}
    override fun onEditorMousePressed(miniMap: MiniMap, event: MouseEvent) {}
    override fun onEditorMouseReleased(miniMap: MiniMap, event: MouseEvent) {}
    override fun onEditorMouseDragged(miniMap: MiniMap, event: MouseEvent) {}
    override fun onEditorMouseMoved(miniMap: MiniMap, event: MouseEvent) {}
    override fun onEditorScroll(miniMap: MiniMap, event: ScrollEvent) {}
    override fun onEditorKeyPressed(miniMap: MiniMap, event: KeyEvent) {}

    override val cursor: Cursor
        get() = Cursor.DEFAULT

    internal fun sortPoints(a: Point2D, b: Point2D): Pair<Point2D, Point2D> {
        val x1 = if (a.x < b.x) a.x else b.x
        val x2 = if (a.x > b.x) a.x else b.x

        val y1 = if (a.y < b.y) a.y else b.y
        val y2 = if (a.y > b.y) a.y else b.y

        return Pair(Point2D(x1, y1), Point2D(x2, y2))
    }
}