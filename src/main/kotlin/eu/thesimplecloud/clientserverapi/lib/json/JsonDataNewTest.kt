package eu.thesimplecloud.clientserverapi.lib.json

import com.google.gson.*
import java.io.*
import java.lang.IllegalArgumentException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


class JsonDataNewTest(private val jsonElement: JsonElement) {


    private var gson: Gson = GSON

    constructor() : this(JsonObject())

    fun append(property: String, value: String?): JsonDataNewTest {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    fun append(property: String, value: Any?): JsonDataNewTest {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.add(property, gson.toJsonTree(value))
        return this
    }

    fun append(property: String, value: Number?): JsonDataNewTest {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
    }

    fun append(property: String, value: Boolean?): JsonDataNewTest {
        if (jsonElement !is JsonObject)
            throw UnsupportedOperationException("Can't append element to JsonPrimitive.")
        jsonElement.addProperty(property, value)
        return this
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
        return if (!jsonElement.has(property)) null else gson.fromJson(jsonElement.get(property), clazz)
    }

    fun <T> getObject(clazz: Class<T>): T {
        return gson.fromJson(getAsJsonString(), clazz)
    }

    fun <T> getObjectOrNull(clazz: Class<T>): T? {
        if (getAsJsonString().isBlank()) return null
        return try {
            gson.fromJson(getAsJsonString(), clazz)
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
        return gson.toJson(jsonElement)
    }

    fun getJsonStringAsBytes(): ByteArray {
        return getAsJsonString().toByteArray(StandardCharsets.UTF_8)
    }

    fun useGsonExclude() {
        this.gson = GSON_EXCLUDE
    }

    companion object {

        val GSON = GsonBuilder().setPrettyPrinting().serializeNulls().create()
        val GSON_EXCLUDE = GsonBuilder().setPrettyPrinting().setExclusionStrategies(GsonExcludeExclusionStrategy()).serializeNulls().create()

        fun fromObject(any: Any): JsonDataNewTest {
            return fromJsonString(GSON.toJson(any), GSON)
        }

        fun fromObjectWithGsonExclude(any: Any): JsonDataNewTest {
            return fromJsonString(GSON_EXCLUDE.toJson(any), GSON_EXCLUDE)
        }

        fun fromJsonFile(path: String): JsonDataNewTest? {
            return fromJsonFile(File(path))
        }

        fun fromJsonFile(file: File): JsonDataNewTest? {
            if (!file.exists()) return null
            return fromJsonString(loadFile(file), GSON)
        }

        fun fromInputStream(inputStream: InputStream): JsonDataNewTest {
            return fromJsonString(loadFromInputStream(inputStream), GSON)
        }

        fun fromInputStreamWithGsonExclude(inputStream: InputStream): JsonDataNewTest {
            return fromJsonString(loadFromInputStream(inputStream), GSON_EXCLUDE)
        }

        fun fromJsonString(string: String, gson: Gson): JsonDataNewTest {
            try {
                val jsonObject = gson.fromJson(string, JsonObject::class.java)
                return JsonDataNewTest(jsonObject)
            } catch (ex: Exception) {
                try {
                    val jsonPrimitive = gson.fromJson(string, JsonPrimitive::class.java)
                    return JsonDataNewTest(jsonPrimitive)
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
}
