package ch.bildspur.anna.scene

import ch.bildspur.anna.controller.timer.Timer
import ch.bildspur.anna.controller.timer.TimerTask
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.renderer.IRenderer

class SceneManager(project: Project) : IRenderer {
    val blackScene = BlackScene(project)
    val startPatternScene = StarPatternScene(project)
    val videoInputScene = VideoInputScene(project)
    val neuralFlowScene = NeuralFlowScene(project)
    val blinkScene = BlinkScene(project)

    val scenes = listOf(blackScene, startPatternScene, videoInputScene, neuralFlowScene, blinkScene)
    var activeScene: BaseScene = blackScene

    private val task = TimerTask(0, { render() }, "SceneManager")

    override val timerTask: TimerTask
        get() = task

    val timer = Timer()

    override fun setup() {
        timer.setup()
        scenes.forEach { it.setup() }

        switchScene(startPatternScene)
    }

    override fun render() {
        timer.update()
    }

    override fun dispose() {
        scenes.forEach { it.dispose() }
    }

    internal fun switchScene(scene: BaseScene) {
        activeScene.stop()
        timer.taskList.remove(activeScene.timerTask)

        activeScene = scene
        activeScene.start()
        timer.addTask(activeScene.timerTask)
    }
}