package ch.bildspur.anna.configuration

import ch.bildspur.anna.Sketch
import ch.bildspur.anna.model.config.AppConfig
import ch.bildspur.anna.model.DataModel
import ch.bildspur.anna.model.Project
import ch.bildspur.anna.model.ann.Weight
import ch.bildspur.anna.model.light.LedArray
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.get
import com.google.gson.*
import processing.core.PVector
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


/**
 * Created by cansik on 11.07.17.
 */
class ConfigurationController {
    companion object {
        @JvmStatic
        val CONFIGURATION_FILE = "${Sketch.URI_NAME}.json"

        @JvmStatic
        val CONFIGURATION_DIR: Path = Paths.get(System.getProperty("user.home"), ".bildspur", Sketch.URI_NAME)

        @JvmStatic
        val CONFIGURATION_PATH: Path = Paths.get(CONFIGURATION_DIR.toString(), CONFIGURATION_FILE)
    }

    val gson: Gson = GsonBuilder()
            .setPrettyPrinting()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(DataModel::class.java, DataModelInstanceCreator())
            .registerTypeAdapter(PVector::class.java, PVectorSerializer())
            .registerTypeAdapter(PVector::class.java, PVectorDeserializer())
            .registerTypeAdapter(LedArray::class.java, LedArrayInstanceCreator())
            .registerTypeAdapter(Weight::class.java, WeigthInstanceCreator())
            .registerTypeAdapterFactory(PostProcessingEnabler())
            .create()

    fun loadAppConfig(): AppConfig {
        if (!Files.exists(CONFIGURATION_DIR)) {
            Files.createDirectories(CONFIGURATION_DIR)
            saveAppConfig(AppConfig())
        }

        return loadData(CONFIGURATION_PATH)
    }

    fun saveAppConfig(config: AppConfig) {
        saveData(CONFIGURATION_PATH, config)
    }

    fun loadProject(projectFile: String): Project {
        val project = loadData<Project>(Paths.get(projectFile))

        // update weights
        project.network.weights.forEach {
            it.network = project.network
        }

        return project
    }

    fun saveProject(projectFile: String, project: Project) {
        saveData(Paths.get(projectFile), project)
    }

    inline fun <reified T : Any> loadData(configFile: Path): T {
        val content = String(Files.readAllBytes(configFile))
        return gson.fromJson(content)
    }

    inline fun <reified T : Any> saveData(configFile: Path, config: T) {
        val content = gson.toJson(config)
        Files.write(configFile, content.toByteArray())
    }

    private inner class PVectorDeserializer : JsonDeserializer<PVector> {
        override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PVector {
            val x = json["x"].asFloat
            val y = json["y"].asFloat
            val z = json["z"].asFloat
            return PVector(x, y, z)
        }
    }

    private inner class PVectorSerializer : JsonSerializer<PVector> {
        override fun serialize(src: PVector, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
            val obj = JsonObject()
            obj.addProperty("x", src.x)
            obj.addProperty("y", src.y)
            obj.addProperty("z", src.z)
            return obj
        }
    }

    private inner class DataModelInstanceCreator : InstanceCreator<DataModel<*>> {
        override fun createInstance(type: Type): DataModel<*> {
            val typeParameters = (type as ParameterizedType).actualTypeArguments
            val defaultValue = typeParameters[0]
            return DataModel(defaultValue as Class<*>)
        }
    }

    private inner class LedArrayInstanceCreator : InstanceCreator<LedArray> {
        override fun createInstance(type: Type): LedArray {
            return LedArray()
        }
    }

    private inner class WeigthInstanceCreator : InstanceCreator<Weight> {
        override fun createInstance(type: Type): Weight {
            return Weight()
        }
    }
}