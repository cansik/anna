package ch.bildspur.anna.view

import ch.bildspur.anna.Sketch
import ch.bildspur.anna.configuration.ConfigurationController
import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.ann.Layer
import ch.bildspur.anna.model.ann.Neuron
import ch.bildspur.anna.model.ann.Weight
import ch.bildspur.anna.model.config.AppConfig
import ch.bildspur.anna.model.light.DmxNode
import ch.bildspur.anna.model.light.DmxUniverse
import ch.bildspur.anna.model.light.Led
import ch.bildspur.anna.model.light.LedArray
import ch.bildspur.anna.scene.SceneManager
import ch.bildspur.anna.view.properties.PropertiesControl
import ch.bildspur.anna.view.util.UITask
import ch.bildspur.anna.view.util.cellFactory
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
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

    @FXML
    lateinit var sceneMenu : Menu

    @FXML
    lateinit var weightTableView : TableView<Weight>

    val weightModels = FXCollections.observableArrayList<Weight>()

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

            // for updating the property view
            propertiesControl.propertyChanged += {
                updateUI()
            }

            setupWeightTableView()
            initSettingsView(project.value, "Project")

            // start processing
            startProcessing()

            sketch.afterRenderReset += {
                setupSceneSwitching()
            }
        }, { updateUI() }, "startup")
    }

    fun setupWeightTableView()
    {
        // setup columns
        val layer1Column = TableColumn<Weight, Int>("Layer 1")
        layer1Column.cellFactory { it.layerIndex1.value }
        weightTableView.columns.add(layer1Column)

        val layer2Column = TableColumn<Weight, Int>("Layer 2")
        layer2Column.cellFactory { it.layerIndex2.value }
        weightTableView.columns.add(layer2Column)

        val neuron1Column = TableColumn<Weight, Int>("Neuron 1")
        neuron1Column.cellFactory { it.neuronIndex1.value }
        weightTableView.columns.add(neuron1Column)

        val neuron2Column = TableColumn<Weight, Int>("Neuron 2")
        neuron2Column.cellFactory { it.neuronIndex2.value }
        weightTableView.columns.add(neuron2Column)

        val led1Column = TableColumn<Weight, Int>("LED 1")
        led1Column.cellFactory { it.ledIndex1.value }
        weightTableView.columns.add(led1Column)

        val led2Column = TableColumn<Weight, Int>("LED 2")
        led2Column.cellFactory { it.ledIndex2.value }
        weightTableView.columns.add(led2Column)

        val led1AddressColumn = TableColumn<Weight, String>("LED 1 DMX")
        led1AddressColumn.cellFactory { it.led1.address.toString() }
        weightTableView.columns.add(led1AddressColumn)

        val led2AddressColumn = TableColumn<Weight, String>("LED 2 DMX")
        led2AddressColumn.cellFactory { it.led2.address.toString() }
        weightTableView.columns.add(led2AddressColumn)


        // set column
        weightTableView.columns.forEach { it.style = "-fx-alignment: CENTER;" }

        // setup items
        weightModels.clear()
        weightModels.addAll(project.value.network.weights)
        weightTableView.items = weightModels

        // setup select
        weightTableView.selectionModel.selectedItemProperty().addListener { o ->
            val item = weightTableView.selectionModel.selectedItem

            if(item != null) {
                initSettingsView(item, "$item")
            }
        }
    }

    fun setupSceneSwitching()
    {
        project.value.sceneSettings.activeScene.onChanged.clear()
        sceneMenu.items.clear()

        val sceneManager = sketch.renderer.filterIsInstance<SceneManager>().first()
        sceneManager.scenes.forEach { scene ->
            val item = RadioMenuItem()

            item.text = scene.name
            item.setOnAction {
                project.value.sceneSettings.activeScene.value = scene.name
            }

            sceneMenu.items.add(item)
        }

        project.value.sceneSettings.activeScene.onChanged += { name ->
            val scene = sceneManager.scenes.find { it.name == name } ?: sceneManager.scenes.first()
            val item = sceneMenu.items.filterIsInstance<RadioMenuItem>().find { it.text == name }
                    ?: sceneMenu.items.filterIsInstance<RadioMenuItem>().first()

            sceneMenu.items.filterIsInstance<RadioMenuItem>().forEach { it.isSelected = false}
            item.isSelected = true

            sceneManager.switchScene(scene)
        }
        project.value.sceneSettings.activeScene.fireLatest()
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
        weightModels.clear()
        weightModels.addAll(project.value.network.weights)
    }

    fun createTestProject() : Project
    {
        val project = Project()
        project.name.value = "Test Project"

        // add dmx structure
        val node = DmxNode()
        node.universes.add(DmxUniverse(0))
        node.universes.add(DmxUniverse(1))
        project.nodes.add(node)

        val pixelPerNode = 8
        val structure =  arrayOf(3, 4, 3, 2)

        var usedAddresses = 0

        // create structure
        structure.forEachIndexed { layerIndex, neuronCount ->
            val layer = Layer()

            // create nodes
            (0 until neuronCount).forEach { i ->
                val addressSize = pixelPerNode * Led.LED_ADDRESS_SIZE
                val ledArray = LedArray(
                        universe = DataModel(0),
                        addressStart =  DataModel(usedAddresses))
                ledArray.ledCount.value = pixelPerNode
                usedAddresses += addressSize

                println("Addresses: $usedAddresses")

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
        updateUI()
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
        initSettingsView(project.value.visualisationSettings, "Visualisation")
    }

    fun onResetRenderer(actionEvent: ActionEvent) {
        resetRenderer()
    }

    fun onExportMappingMask(actionEvent: ActionEvent) {
        val fileChooser = FileChooser()
        fileChooser.initialFileName = "map.png"
        fileChooser.title = "Save map..."
        fileChooser.extensionFilters.addAll(FileChooser.ExtensionFilter("PNG", "*.png"))

        val result = fileChooser.showSaveDialog(primaryStage)

        if (result != null) {
            UITask.run({
                val sceneManager = sketch.renderer.filterIsInstance<SceneManager>().first()
                sceneManager.videoInputScene.createMap().save(result.path)
            }, { updateUI() }, "save mapping mask")
        }
    }

    fun onExportConnections(actionEvent: ActionEvent) {
        println("not implemented!")
    }

    fun onShowSyphonSetting(actionEvent: ActionEvent) {
        initSettingsView(project.value.syphonSettings, "Syphon")
    }

    fun onAddWeightClicked(actionEvent: ActionEvent) {
        UITask.run({
            val weight = Weight(0, 0, 0, 0, 0, 0, project.value.network)
            project.value.network.weights.add(weight)
            initSettingsView(weight, "New Weight")
        }, {updateUI()}, ("add weight"))
    }

    fun onRemoveWeightClicked(actionEvent: ActionEvent) {
        UITask.run({
            val item = weightTableView.selectionModel.selectedItem

            if(item != null)
                project.value.network.weights.remove(item)
        }, {updateUI()}, ("remove weight"))
    }
}
