/*
 * MIT License
 *
 * Copyright (C) 2020 Frederick Baier
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */

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