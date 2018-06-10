package ch.bildspur.anna.view.control.map.tool

import ch.bildspur.anna.view.control.map.MiniMap
import javafx.scene.Cursor
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent

/**
 * Created by cansik on 25.01.17.
 */
interface IEditorTool {
    fun onCanvasMouseClicked(miniMap: MiniMap, event: MouseEvent)
    fun onCanvasMousePressed(miniMap: MiniMap, event: MouseEvent)
    fun onCanvasMouseReleased(miniMap: MiniMap, event: MouseEvent)
    fun onCanvasMouseDragged(miniMap: MiniMap, event: MouseEvent)
    fun onCanvasMouseMoved(miniMap: MiniMap, event: MouseEvent)
    fun onCanvasScroll(miniMap: MiniMap, event: ScrollEvent)
    fun onCanvasKeyPressed(miniMap: MiniMap, event: KeyEvent)

    fun onEditorMouseClicked(miniMap: MiniMap, event: MouseEvent)
    fun onEditorMousePressed(miniMap: MiniMap, event: MouseEvent)
    fun onEditorMouseReleased(miniMap: MiniMap, event: MouseEvent)
    fun onEditorMouseDragged(miniMap: MiniMap, event: MouseEvent)
    fun onEditorMouseMoved(miniMap: MiniMap, event: MouseEvent)
    fun onEditorScroll(miniMap: MiniMap, event: ScrollEvent)
    fun onEditorKeyPressed(miniMap: MiniMap, event: KeyEvent)

    val cursor: Cursor
        get
}