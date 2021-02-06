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

package eu.thesimplecloud.clientserverapi.lib.list.impl

import eu.thesimplecloud.clientserverapi.lib.list.ISyncList
import eu.thesimplecloud.clientserverapi.lib.list.WrappedListItem
import eu.thesimplecloud.clientserverapi.lib.list.listener.ISyncListListenerAdapter
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.Identifiable
import eu.thesimplecloud.clientserverapi.lib.util.JsonSerializedClass
import eu.thesimplecloud.jsonlib.JsonLib
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 05/02/2021
 * Time: 11:46
 * @author Frederick Baier
 */
abstract class AbstractSyncList<T : Identifiable> : ISyncList<T> {


    protected val list = CopyOnWriteArrayList<WrappedListItem<T>>()
    protected val listeners = CopyOnWriteArrayList<ISyncListListenerAdapter<T>>()


    override fun addElement(element: T, fromPacket: Boolean): ICommunicationPromise<Unit> {
        list.removeIf { it.element.getIdentifier() == element.getIdentifier() }
        list.add(WrappedListItem(element))

        this.listeners.forEach { it.onElementAdded(element) }
        return CommunicationPromise.UNIT_PROMISE
    }

    override fun removeElement(identifier: Any, fromPacket: Boolean): ICommunicationPromise<Unit> {
        val value = getValueByIdentifier(identifier) ?: return CommunicationPromise.failed(NoSuchElementException())
        list.remove(value)

        this.listeners.forEach { it.onElementRemoved(value.element) }
        return CommunicationPromise.UNIT_PROMISE
    }


    fun updateElement0(cachedValue: T): JsonLib {
        val identifier = cachedValue.getIdentifier()
        val wrappedListItem = getValueByIdentifier(identifier)
        if (wrappedListItem == null || wrappedListItem.element !== cachedValue)
            throw IllegalArgumentException("Cannot update a value that is not contained in the list")
        val oldValue = wrappedListItem.currentJson.getObject(cachedValue::class.java)

        this.listeners.forEach { it.onElementUpdated(oldValue, cachedValue) }

        return wrappedListItem.updateToItem(cachedValue)
    }


    override fun applyUpdate(json: JsonLib): ICommunicationPromise<Unit> {
        val updatesJson = json.getProperty("updates")!!
        val identifier = json.getProperty("identifier")!!.getObject(JsonSerializedClass::class.java).getValue()
        val valueByIdentifier = getValueByIdentifier(identifier)!!
        valueByIdentifier.applyChangesJson(updatesJson)
        return CommunicationPromise.UNIT_PROMISE
    }

    protected fun getValueByIdentifier(identifier: Any): WrappedListItem<T>? {
        return this.list.firstOrNull { it.element.getIdentifier() == identifier }
    }

    override fun addListener(listener: ISyncListListenerAdapter<T>) {
        this.listeners.add(listener)
    }

    override fun removeListener(listener: ISyncListListenerAdapter<T>) {
        this.listeners.remove(listener)
    }

    override fun getAllElements(): List<T> {
        return this.list.map { it.element }
    }

    fun setAllElements(elements: List<T>) {
        this.list.clear()
        elements.forEach { this.list.add(WrappedListItem(it)) }
    }

}
