package eu.thesimplecloud.clientserverapi.lib.json

import com.google.gson.*
import java.io.*
import java.lang.IllegalArgumentException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


class JsonData(val jsonElement: JsonElement) {

    private var exclude: Boolean = false

    constructor() : this(JsonObject())

    fun append(property: String, value: String?): JsonData {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    fun append(property: String, value: Any?): JsonData {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.add(property, getGsonToUse().toJsonTree(value))
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
        return jsonElement.get(name)?.let { JsonData(it) }
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

    fun <T> getObject(property: String, clazz: Class<T>): T? {
        if (jsonElement !is JsonObject) throw UnsupportedOperationException("Can't get element from JsonPrimitive.")
        if (clazz == JsonData::class.java) {
            return getProperty(property) as T
        }
        return if (!jsonElement.has(property)) null else getGsonToUse().fromJson(jsonElement.get(property), clazz)
    }

    fun <T> getObject(clazz: Class<T>): T {
        return getGsonToUse().fromJson(getAsJsonString(), clazz)
    }

    fun <T> getObjectOrNull(clazz: Class<T>): T? {
        if (getAsJsonString().isBlank()) return null
        return try {
            getGsonToUse().fromJson(getAsJsonString(), clazz)
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
        return getGsonToUse().toJson(jsonElement)
    }

    fun getJsonStringAsBytes(): ByteArray {
        return getAsJsonString().toByteArray(StandardCharsets.UTF_8)
    }

    fun useGsonExclude(): JsonData {
        this.exclude = true
        return this
    }

    private fun getGsonToUse(): Gson {
        return if (exclude) GSON_EXCLUDE else GSON
    }


    companion object {

        val GSON = GsonBuilder().registerTypeAdapter(JsonData::class.java, JsonDataSerializer()).setPrettyPrinting().serializeNulls().create()
        val GSON_EXCLUDE = GsonBuilder().registerTypeAdapter(JsonData::class.java, JsonDataSerializer()).setPrettyPrinting().setExclusionStrategies(GsonExcludeExclusionStrategy()).serializeNulls().create()

        fun fromObject(any: Any): JsonData {
            return fromJsonString(GSON.toJson(any))
        }

        fun fromObjectWithGsonExclude(any: Any): JsonData {
            return fromJsonStringWithGsonExclude(GSON_EXCLUDE.toJson(any))
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

        fun fromInputStreamWithGsonExclude(inputStream: InputStream): JsonData {
            return fromJsonStringWithGsonExclude(loadFromInputStream(inputStream))
        }

        fun fromJsonString(string: String): JsonData {
            return this.fromJsonString0(string, GSON)
        }

        fun fromJsonStringWithGsonExclude(string: String): JsonData {
            return this.fromJsonString0(string, GSON_EXCLUDE)
        }

        fun fromJsonString0(string: String, gson: Gson): JsonData {
            try {
                val jsonObject = gson.fromJson(string, JsonObject::class.java)
                return JsonData(jsonObject)
            } catch (ex: Exception) {
                try {
                    val jsonPrimitive = gson.fromJson(string, JsonPrimitive::class.java)
                    return JsonData(jsonPrimitive)
                } catch (ex: Exception) {
                    throw IllegalArgumentException("Can't parse string $string", ex)
                }
            }
        }

        private fun loadFromInputStream(inputStream: InputStream): String {
            try {
                val data = ByteArray(inputStream.available())
                inputStream.read(data)
                inputStream.close()
                return String(data, Charset.defaultCharset())
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return ""
        }


        private fun loadFile(file: File): String {
            if (!file.exists())
                return ""
            try {
                val fis = FileInputStream(file)
                val data = ByteArray(file.length().toInt())
                fis.read(data)
                fis.close()

                return String(data, Charset.defaultCharset())
            } catch (e: IOException) {
                // TODO: handle exception
            }

            return ""
        }
    }

    override fun toString(): String {
        return getAsJsonString()
    }
}
