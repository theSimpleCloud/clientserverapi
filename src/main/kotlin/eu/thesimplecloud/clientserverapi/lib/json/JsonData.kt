package eu.thesimplecloud.clientserverapi.lib.json

import com.google.gson.*
import java.io.*
import java.nio.charset.StandardCharsets


class JsonData private constructor(val jsonElement: JsonElement, private val currentGson: Gson) {


    @Deprecated("Create via static method instead", ReplaceWith("JsonData.empty()"))
    constructor() : this(JsonObject(), GSON)

    fun append(property: String, value: String?): JsonData {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    fun append(property: String, value: Any?): JsonData {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.add(property, this.currentGson.toJsonTree(value))
        return this
    }

    fun append(property: String, value: Number?): JsonData {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    fun append(property: String, value: Boolean?): JsonData {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    /**
     * Returns the property found by the specified name
     */
    fun getProperty(name: String): JsonData? {
        if (jsonElement !is JsonObject)
            return null
        return jsonElement.get(name)?.let { JsonData(it, currentGson) }
    }

    /**
     * Returns a [JsonData] found by the specified path
     * The path will be split with .
     */
    fun getPath(path: String): JsonData? {
        val array = path.split(".")
        var currentJsonData: JsonData? = this
        for (property in array) {
            currentJsonData = currentJsonData?.getProperty(property)
            if (currentJsonData == null) {
                return null
            }
        }
        return currentJsonData
    }

    fun getInt(property: String): Int? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.get(property).asInt
    }

    fun getLong(property: String): Long? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.get(property).asLong
    }

    fun getDouble(property: String): Double? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.get(property).asDouble
    }

    fun getFloat(property: String): Float? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.get(property).asFloat
    }

    fun getBoolean(property: String): Boolean? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.get(property).asBoolean
    }

    fun getAsJsonArray(property: String): JsonArray? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.get(property).asJsonArray
    }


    fun <T> getObject(property: String, clazz: Class<T>): T? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        if (clazz == JsonData::class.java) {
            return getProperty(property) as T
        }
        return if (!jsonElement.has(property)) null else this.currentGson.fromJson(jsonElement.get(property), clazz)
    }

    fun <T> getObject(clazz: Class<T>): T {
        return this.currentGson.fromJson(getAsJsonString(), clazz)
    }

    fun <T> getObjectOrNull(clazz: Class<T>): T? {
        if (getAsJsonString().isBlank()) return null
        return try {
            this.currentGson.fromJson(getAsJsonString(), clazz)
        } catch (ex: Exception) {
            null
        }

    }

    fun getString(property: String): String? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        return if (!jsonElement.has(property)) null else jsonElement.get(property).asString
    }


    fun saveAsFile(path: String) {
        saveJsonElementAsFile(File(path))
    }

    fun saveAsFile(file: File) {
        saveJsonElementAsFile(file)
    }

    fun saveJsonElementAsFile(path: String): Boolean {
        return saveJsonElementAsFile(File(path))
    }

    fun saveJsonElementAsFile(file: File): Boolean {
        val dir = file.parentFile
        if (dir != null && !dir.exists()) {
            dir.mkdirs()
        }
        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(getJsonStringAsBytes())
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: IOException) {
            return false
        }

        return false
    }


    fun getAsJsonString(): String {
        return this.currentGson.toJson(jsonElement)
    }

    fun getJsonStringAsBytes(): ByteArray {
        return getAsJsonString().toByteArray(StandardCharsets.UTF_8)
    }


    companion object {

        val GSON = GsonCreator().excludeAnnotations(GsonExclude::class.java).create()

        fun empty() = empty(GSON)

        fun empty(gson: Gson) = JsonData(JsonObject(), gson)

        fun fromJsonElement(jsonElement: JsonElement): JsonData {
            return JsonData(jsonElement, GSON)
        }

        fun fromObject(any: Any): JsonData {
            return fromJsonString(GSON.toJson(any))
        }

        fun fromObject(any: Any, gson: Gson): JsonData {
            return fromJsonString(gson.toJson(any), gson)
        }

        fun fromJsonFile(path: String): JsonData? {
            return fromJsonFile(File(path))
        }

        fun fromJsonFile(file: File): JsonData? {
            if (!file.exists()) return null
            return fromJsonString(loadFile(file))
        }

        fun fromInputStream(inputStream: InputStream): JsonData {
            return fromJsonString(loadFromInputStream(inputStream))
        }

        fun fromInputStream(inputStream: InputStream, gson: Gson): JsonData {
            return fromJsonString(loadFromInputStream(inputStream), gson)
        }

        fun fromJsonString(string: String): JsonData {
            return this.fromJsonString(string, GSON)
        }

        fun fromJsonString(string: String, gson: Gson): JsonData {
            return try {
                val jsonObject = gson.fromJson(string, JsonObject::class.java)
                JsonData(jsonObject, gson)
            } catch (ex: Exception) {
                try {
                    val jsonPrimitive = gson.fromJson(string, JsonArray::class.java)
                    JsonData(jsonPrimitive, gson)
                } catch (ex: java.lang.Exception) {
                    try {
                        val jsonPrimitive = gson.fromJson(string, JsonPrimitive::class.java)
                        JsonData(jsonPrimitive, gson)
                    } catch (ex: Exception) {
                        throw IllegalArgumentException("Can't parse string $string", ex)
                    }
                }
            }
        }

        private fun loadFromInputStream(inputStream: InputStream): String {
            try {
                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                inputStream.close()
                return String(data, Charsets.UTF_8)
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return ""
        }


        private fun loadFile(file: File): String {
            if (!file.exists())
                return ""
            return loadFromInputStream(FileInputStream(file))
        }
    }

    override fun toString(): String {
        return getAsJsonString()
    }
}
