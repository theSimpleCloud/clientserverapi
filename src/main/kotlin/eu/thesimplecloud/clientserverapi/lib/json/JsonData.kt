package eu.thesimplecloud.clientserverapi.lib.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import java.io.*
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets


class JsonData(private val jsonObject: JsonObject) {

    private var gson: Gson = GSON

    val asJsonString: String
        get() = gson.toJson(jsonObject)

    val jsonStringAsBytes: ByteArray
        get() = asJsonString.toByteArray(StandardCharsets.UTF_8)

    constructor(): this(JsonObject())

    fun useExclusionStrategy(): JsonData {
        if (gson == Companion.GSON_NOT_PRETTY || gson == Companion.GSON_NOT_PRETTY_EXCLUSION) {
            this.gson = Companion.GSON_NOT_PRETTY_EXCLUSION
        } else {
            this.gson = Companion.GSON_EXCLUSION
        }
        return this
    }

    fun unpretty(): JsonData {
        if (gson == Companion.GSON_EXCLUSION || gson == Companion.GSON_NOT_PRETTY_EXCLUSION) {
            this.gson = Companion.GSON_NOT_PRETTY_EXCLUSION
        } else {
            this.gson = Companion.GSON_NOT_PRETTY
        }
        return this
    }

    fun append(property: String?, value: String?): JsonData {
        if (property == null)
            return this
        jsonObject.addProperty(property, value)
        return this
    }

    fun append(property: String?, value: Any?): JsonData {
        if (property == null)
            return this
        jsonObject.add(property, gson.toJsonTree(value))
        return this
    }

    fun append(property: String?, value: Number?): JsonData {
        if (property == null)
            return this
        jsonObject.addProperty(property, value)
        return this
    }

    fun append(property: String?, value: Boolean?): JsonData {
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
            fileOutputStream.write(jsonStringAsBytes)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: IOException) {
            return false
        }

        return false
    }


    fun getAsJsonString(`object`: Any): String {
        return gson.toJson(gson.toJsonTree(`object`))
    }

    companion object {

        private val GSON_EXCLUSION = GsonBuilder().setPrettyPrinting().setExclusionStrategies(GsonExcludeExclusionStrategy()).serializeNulls().create()

        private val GSON = GsonBuilder().setPrettyPrinting().serializeNulls().create()

        private val GSON_NOT_PRETTY = GsonBuilder().setExclusionStrategies(GsonExcludeExclusionStrategy()).serializeNulls().create()

        private val GSON_NOT_PRETTY_EXCLUSION = GsonBuilder().setExclusionStrategies(GsonExcludeExclusionStrategy()).serializeNulls().create()


        fun fromJsonFile(path: String): JsonData {
            return fromJsonFile(File(path))
        }

        fun fromJsonFile(file: File): JsonData {
            return fromJsonString(loadFile(file))
        }

        fun fromInputStream(inputStream: InputStream): JsonData {
            return fromJsonString(loadFromInputStream(inputStream))
        }

        fun fromJsonString(string: String): JsonData {
            val jsonObject = GSON.fromJson(string, JsonObject::class.java)
            return JsonData(jsonObject)
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
