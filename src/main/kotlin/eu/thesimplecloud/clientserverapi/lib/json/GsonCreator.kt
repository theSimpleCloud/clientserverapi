package eu.thesimplecloud.clientserverapi.lib.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlin.reflect.KClass

class GsonCreator {

    private val builder = GsonBuilder()
            .registerTypeAdapter(JsonData::class.java, JsonDataSerializer())
            .setPrettyPrinting()
            .serializeNulls()

    fun excludeAnnotations(vararg annotationClasses: KClass<out Annotation>): GsonCreator {
        this.excludeAnnotations(*annotationClasses.map { it.java }.toTypedArray())
        return this
    }

    fun excludeAnnotations(vararg annotationClasses: Class<out Annotation>): GsonCreator {
        this.builder.setExclusionStrategies(AnnotationExclusionStrategy(*annotationClasses))
        return this
    }

    fun create(): Gson {
        return this.builder.create()
    }


}