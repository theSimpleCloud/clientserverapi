package eu.thesimplecloud.clientserverapi.lib.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.*
import java.lang.IllegalArgumentException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


class JsonDataOld(private val jsonObject: JsonObject) {

    private var gson: Gson = GSON

    constructor() : this(JsonObject())

    fun append(property: String?, value: String?): JsonDataOld {
        if (property == null)
            return this
        jsonObject.addProperty(property, value)
        return this
    }

    fun append(property: String?, value: Any?): JsonDataOld {
        if (property == null)
            return this
        jsonObject.add(property, gson.toJsonTree(value))
        return this
    }

    fun append(property: String?, value: Number?): JsonDataOld {
        if (property == null)
            return this
        jsonObject.addProperty(property, value)
        return this
    }

    fun append(property: String?, value: Boolean?): JsonDataOld {
        if (property == null)
            return this
        jsonObject.addProperty(property, value)
        return this
    }

    fun getInt(property: String): Int? {
        return if (!jsonObject.has(property)) null else jsonObject.get(property).asInt
    }

    fun getLong(property: String): Long? {
        return if (!jsonObject.has(property)) null else jsonObject.get(property).asLong
    }

    fun getDouble(property: String): Double? {
        return if (!jsonObject.has(property)) null else jsonObject.get(property).asDouble
    }

    fun getFloat(property: String): Float? {
        return if (!jsonObject.has(property)) null else jsonObject.get(property).asFloat
    }

    fun getBoolean(property: String): Boolean? {
        return if (!jsonObject.has(property)) null else jsonObject.get(property).asBoolean
    }

    fun <T> getObject(property: String, clazz: Class<T>): T? {
        return if (!jsonObject.has(property)) null else gson.fromJson(jsonObject.get(property), clazz)
    }

    fun <T> getObject(clazz: Class<T>): T? {
        return gson.fromJson(getAsJsonString(), clazz)
    }

    fun getString(property: String): String? {
        return if (!jsonObject.has(property)) null else jsonObject.get(property).asString
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
        return gson.toJson(jsonObject)
    }

    fun getJsonStringAsBytes(): ByteArray {
        return getAsJsonString().toByteArray(StandardCharsets.UTF_8)
    }

    companion object {

        private val GSON = GsonBuilder().setPrettyPrinting().setExclusionStrategies(GsonExcludeExclusionStrategy()).serializeNulls().create()

        fun fromObject(any: Any): JsonDataOld {
            return fromJsonString(GSON.toJson(any))
        }

        fun fromJsonFile(path: String): JsonDataOld {
            return fromJsonFile(File(path))
        }

        fun fromJsonFile(file: File): JsonDataOld {
            return fromJsonString(loadFile(file))
        }

        fun fromInputStream(inputStream: InputStream): JsonDataOld {
            return fromJsonString(loadFromInputStream(inputStream))
        }

        fun fromJsonString(string: String): JsonDataOld {
            try {
                val jsonObject = GSON.fromJson(string, JsonObject::class.java)
                return JsonDataOld(jsonObject)
            } catch (ex: Exception) {
                throw IllegalArgumentException("Can't parse string $string", ex)
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
