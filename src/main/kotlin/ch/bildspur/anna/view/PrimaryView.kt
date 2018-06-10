package ch.bildspur.anna.view

import ch.bildspur.anna.Sketch
import ch.bildspur.anna.configuration.ConfigurationController
import ch.bildspur.anna.model.AppConfig
import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.view.util.UITask
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import processing.core.PApplet
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread


class PrimaryView {
    lateinit var primaryStage: Stage

    @FXML
    lateinit var root: BorderPane

    val configuration = ConfigurationController()

    lateinit var appConfig: AppConfig

    val project = DataModel(Project())

    lateinit var sketch: Sketch

    lateinit var processingThread: Thread

    init {
    }

    fun setupView() {

        // setup ui
        UITask.run({
            // load app config
            appConfig = configuration.loadAppConfig()

            // create or load configuration
            if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile)))
                project.value = configuration.loadProject(appConfig.projectFile)
            else
                project.value = Project()

            // start processing
            startProcessing()
        }, { updateUI() }, "startup")
    }

    fun startProcessing() {
        sketch = Sketch()

        project.onChanged += {
            sketch.project.value = project.value
        }
        project.fire()

        processingThread = thread {
            // run processing app
            PApplet.runSketch(arrayOf("Sketch "), sketch)
            println("processing quit")
        }
    }

    fun updateUI() {
    }

    fun newProject(e: ActionEvent) {
        // reset current project
        UITask.run({
            appConfig.projectFile = ""

            project.value = Project()
            resetRenderer()
        }, { updateUI() }, "new project")
    }

    fun loadProject(e: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.title = "Select project to load"
        fileChooser.initialFileName = ""
        fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("JSON", "*.json")
        )

        val result = fileChooser.showOpenDialog(primaryStage)

        if (result != null) {
            UITask.run({
                project.value = configuration.loadProject(result.path)
                appConfig.projectFile = result.path
                configuration.saveAppConfig(appConfig)

                resetRenderer()
            }, { updateUI() }, "load project")
        }
    }

    fun resetRenderer() {
        sketch.proposeResetRenderer()
    }

    fun rebuildRenderer() {
        sketch.renderer.forEach { it.setup() }
    }

    fun saveProjectAs(e: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.initialFileName = ""
        fileChooser.title = "Save project..."
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("JSON", "*.json"))

        val result = fileChooser.showSaveDialog(primaryStage)

        if (result != null) {
            UITask.run({
                configuration.saveProject(result.path, project.value)
                appConfig.projectFile = result.path
                configuration.saveAppConfig(appConfig)
            }, { updateUI() }, "save project")
        }
    }

    fun saveProject(e: ActionEvent) {
        if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile))) {
            UITask.run({
                configuration.saveProject(appConfig.projectFile, project.value)
                configuration.saveAppConfig(appConfig)
            }, { updateUI() }, "save project")
        }
    }
}
