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

package eu.thesimplecloud.clientserverapi.lib.list

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import eu.thesimplecloud.clientserverapi.lib.util.Identifiable
import eu.thesimplecloud.clientserverapi.lib.util.JsonSerializedClass
import eu.thesimplecloud.jsonlib.JsonLib

/**
 * Created by IntelliJ IDEA.
 * Date: 31/01/2021
 * Time: 21:59
 * @author Frederick Baier
 */
class WrappedListItem<T : Identifiable>(
    element: T
) {
    @Volatile
    var element: T = element
        private set

    @Volatile
    var currentJson: JsonLib = JsonLib.fromObject(element)
        private set

    /**
     * Updates the value and returns a changes json
     */
    fun updateToItem(item: T): JsonLib {
        val oldJson = this.currentJson
        this.element = item
        this.currentJson = JsonLib.fromObject(item)

        return generateUpdateJsonWithIdentifier(oldJson, this.currentJson)
    }

    private fun generateUpdateJsonWithIdentifier(oldJson: JsonLib, newJson: JsonLib): JsonLib {
        val changesJson = generateChangesJson(oldJson, newJson)
        changesJson.append("identifier", JsonSerializedClass(this.element.getIdentifier()))
        return changesJson
    }

    private fun generateChangesJson(oldJson: JsonLib, newJson: JsonLib): JsonLib {
        val newJsonObject = newJson.jsonElement as JsonObject
        val oldJsonObject = oldJson.jsonElement as JsonObject

        val allNewKeys = newJsonObject.entrySet().map { it.key }
        val allOldKeys = oldJsonObject.entrySet().map { it.key }

        val creations = allNewKeys.toMutableList()
        creations.removeAll(allOldKeys)
        val deletions = allOldKeys.toMutableList()
        deletions.removeAll(allNewKeys)

        val otherProperties = allNewKeys.union(allOldKeys).toMutableList()
        otherProperties.removeAll(creations.union(deletions))
        val updatedProperties = otherProperties.filter { newJsonObject.get(it) != oldJsonObject.get(it) }

        val finalJsonLib = JsonLib.empty()
        finalJsonLib.append("deletions", deletions)
        finalJsonLib.append("creations", getValuesForProperties(creations, newJsonObject))
        finalJsonLib.append("updates", getValuesForProperties(updatedProperties, newJsonObject))
        return finalJsonLib
    }

    private fun getValuesForProperties(properties: List<String>, jsonObject: JsonObject): Map<String, JsonElement> {
        return properties.map { it to jsonObject.get(it) }.toMap()
    }

    fun applyChangesJson(updatesJson: JsonLib) {
        val currentJson = JsonLib.fromObject(this.element)
        val currentJsonObject = currentJson.jsonElement as JsonObject
        val updatesJsonObject = updatesJson.jsonElement as JsonObject

        val allUpdateProperties = updatesJsonObject.entrySet().map { it.key }

        allUpdateProperties.forEach { currentJsonObject.remove(it) }
        allUpdateProperties.forEach { currentJsonObject.add(it, updatesJsonObject[it]) }
        updateToItem(JsonLib.fromJsonElement(currentJsonObject).getObject(this.element::class.java))
    }

}