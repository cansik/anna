package ch.bildspur.anna.view

import ch.bildspur.anna.Sketch
import ch.bildspur.anna.configuration.ConfigurationController
import ch.bildspur.anna.model.AppConfig
import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.ann.Layer
import ch.bildspur.anna.model.ann.Neuron
import ch.bildspur.anna.model.ann.Weight
import ch.bildspur.anna.model.light.DmxNode
import ch.bildspur.anna.model.light.DmxUniverse
import ch.bildspur.anna.model.light.Led
import ch.bildspur.anna.model.light.LedArray
import ch.bildspur.anna.view.properties.PropertiesControl
import ch.bildspur.anna.view.util.UITask
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.Alert
import javafx.scene.control.Alert.AlertType
import javafx.scene.control.TitledPane
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Stage
import processing.core.PApplet
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.concurrent.thread
import kotlin.system.exitProcess


class PrimaryView {
    lateinit var primaryStage: Stage

    @FXML
    lateinit var root: BorderPane

    @FXML
    lateinit var propertiesPane : TitledPane

    val propertiesControl = PropertiesControl()

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
            propertiesPane.content = propertiesControl

            // create or load configuration
            if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile)))
                project.value = configuration.loadProject(appConfig.projectFile)
            else
                project.value = Project()

            // create test project
            // todo: remove this for real use case
            //project.value = createTestProject()

            // for updating the property view
            propertiesControl.propertyChanged += {
                updateUI()
            }

            initSettingsView(project.value, "Project")

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

    fun createTestProject() : Project
    {
        val project = Project()
        project.name.value = "Chateau Project"

        // add dmx structure
        val node = DmxNode()
        node.universes.add(DmxUniverse(0))
        node.universes.add(DmxUniverse(1))
        node.universes.add(DmxUniverse(2))
        node.universes.add(DmxUniverse(3))
        project.nodes.add(node)

        val ledsPerNode = 8
        val structure =  arrayOf(3, 4, 3, 2)

        // create structure
        structure.forEach {
            val layer = Layer()

            // create nodes
            (0 until it).forEach { i ->
                val ledArray = LedArray(
                        universe = DataModel(i),
                        addressStart =  DataModel(i * ledsPerNode * Led.LED_ADDRESS_SIZE))
                ledArray.ledCount.value = ledsPerNode

                val neuron = Neuron(ledArray)
                layer.neurons.add(neuron)
            }

            project.network.layers.add(layer)
        }

        // create default connections on first n ledArray
        (0 until project.network.layers.size - 1).forEach {i ->
            val layer1Index = i
            val layer2Index = i + 1
            val layer3Index = i + 2

            val l1 = project.network.layers[layer1Index]
            val l2 = project.network.layers[layer2Index]
            val l3Size = if(project.network.layers.size > layer3Index) project.network.layers[layer3Index].neurons.size else 0

            // add weights per neuron
            l1.neurons.forEachIndexed {li1, n1 ->
                l2.neurons.forEachIndexed {li2, n2 ->
                    val weight = Weight(layer1Index, li1, li2, layer2Index, li2,li1 + l3Size, project.network)
                    project.network.weights.add(weight)
                }
            }
        }

        return project
    }

    private fun initSettingsView(value : Any, name : String)
    {
        propertiesPane.text = name
        propertiesControl.initView(value)
    }

    fun resetRenderer() {
        sketch.proposeResetRenderer()
    }

    fun rebuildRenderer() {
        sketch.renderer.forEach { it.setup() }
    }

    fun onClose(e: ActionEvent) {
        sketch.exit()
        exitProcess(0)
    }

    fun onNewProject(e: ActionEvent) {
        // reset current project
        UITask.run({
            appConfig.projectFile = ""

            project.value = Project()
            resetRenderer()

            initSettingsView(project.value, "Project")
        }, { updateUI() }, "new project")
    }

    fun onNewChateauProject(actionEvent: ActionEvent) {
        // reset current project
        UITask.run({
            appConfig.projectFile = ""

            project.value = createTestProject()
            resetRenderer()

            initSettingsView(project.value, "Project")
        }, { updateUI() }, "new chateau project")
    }

    fun onOpenProject(e: ActionEvent) {
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
            }, { updateUI() }, "open project")
        }
    }

    fun onSaveProjectAs(e: ActionEvent) {
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

    fun onSaveProject(e: ActionEvent) {
        if (Files.exists(Paths.get(appConfig.projectFile)) && !Files.isDirectory(Paths.get(appConfig.projectFile))) {
            UITask.run({
                configuration.saveProject(appConfig.projectFile, project.value)
                configuration.saveAppConfig(appConfig)
            }, { updateUI() }, "save project")
        }
    }

    fun onShowAbout(e : ActionEvent)
    {
        val alert = Alert(AlertType.INFORMATION)
        alert.title = "About"
        alert.headerText = "${Sketch.NAME} - ${Sketch.VERSION}"
        alert.contentText = "Developed by Florian Bruggisser 2018.\nwww.bildspur.ch\n\nURI: ${Sketch.URI_NAME}"

        alert.showAndWait()
    }

    fun onShowProjectSetting(actionEvent: ActionEvent) {
        initSettingsView(project.value, "Project")
    }

    fun onShowLightSetting(actionEvent: ActionEvent) {
        initSettingsView(project.value.light, "Light")
    }

    fun onShowVisualisationSetting(actionEvent: ActionEvent) {
        initSettingsView(project.value.networkViewSettings, "Visualisation")
    }
}
