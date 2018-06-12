package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.Timer
import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.light.LedArray
import ch.bildspur.anna.renderer.IRenderer

class SceneManager(val project: Project) : IRenderer {
    val blackScene = BlackScene(project.network)

    var activeScene: BaseScene = blackScene

    private val task = TimerTask(0, { render() }, "SceneManager")

    override val timerTask: TimerTask
        get() = task

    val timer = Timer()

    override fun setup() {
        timer.setup()
        initScene(blackScene)
    }

    override fun render() {
        timer.update()
    }

    override fun dispose() {
        blackScene.dispose()
    }

    internal fun initScene(scene: BaseScene) {
        activeScene.stop()
        timer.taskList.remove(activeScene.timerTask)

        activeScene = scene
        activeScene.setup()
        timer.addTask(activeScene.timerTask)
    }
}