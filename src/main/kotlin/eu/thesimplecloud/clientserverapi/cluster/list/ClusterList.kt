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

package eu.thesimplecloud.clientserverapi.cluster.list

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.list.listener.IClusterListListenerAdapter
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIOAddElementToClusterList
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIORemoveElementFromClusterList
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIOUpdateClusterListElement
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.JsonSerializedClass
import eu.thesimplecloud.jsonlib.JsonLib
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Created by IntelliJ IDEA.
 * Date: 31/01/2021
 * Time: 20:56
 * @author Frederick Baier
 */
class ClusterList<T : IClusterListItem>(
    private val cluster: ICluster,
    private val name: String,
    private val arrayClass: Class<Array<Any>>
) : IClusterList<T> {

    private val list = CopyOnWriteArrayList<WrappedListItem<T>>()
    private val listeners = CopyOnWriteArrayList<IClusterListListenerAdapter<T>>()

    override fun addElement(element: T, fromPacket: Boolean): ICommunicationPromise<Unit> {
        list.removeIf { it.clusterListItem.getIdentifier() == element.getIdentifier() }
        list.add(WrappedListItem(element))

        this.listeners.forEach { it.onElementAdded(element) }
        if (!fromPacket) {
            return cluster.sendPacketToAllNodes(PacketIOAddElementToClusterList(name, element))
        }
        return CommunicationPromise.UNIT_PROMISE
    }

    override fun removeElement(identifier: Any, fromPacket: Boolean): ICommunicationPromise<Unit> {
        val value = getValueByIdentifier(identifier) ?: return CommunicationPromise.failed(NoSuchElementException())
        list.remove(value)

        this.listeners.forEach { it.onElementRemoved(value.clusterListItem) }
        if (!fromPacket) {
            return cluster.sendPacketToAllNodes(PacketIORemoveElementFromClusterList(name, identifier))
        }
        return CommunicationPromise.UNIT_PROMISE
    }

    override fun updateElement(cachedValue: T): ICommunicationPromise<Unit> {
        val identifier = cachedValue.getIdentifier()
        val wrappedListItem = getValueByIdentifier(identifier)
        if (wrappedListItem == null || wrappedListItem.clusterListItem !== cachedValue)
            throw IllegalArgumentException("Cannot update a value that is not contained in the list")
        val oldValue = wrappedListItem.currentJson.getObject(cachedValue::class.java)

        this.listeners.forEach { it.onElementUpdated(oldValue, cachedValue) }

        val json = wrappedListItem.updateToItem(cachedValue)
        return cluster.sendPacketToAllNodes(PacketIOUpdateClusterListElement(name, json))
    }

    private fun getValueByIdentifier(identifier: Any): WrappedListItem<T>? {
        return this.list.firstOrNull { it.clusterListItem.getIdentifier() == identifier }
    }

    override fun applyUpdate(json: JsonLib) {
        val updatesJson = json.getProperty("updates")!!
        val identifier = json.getProperty("identifier")!!.getObject(JsonSerializedClass::class.java).getValue()
        val valueByIdentifier = getValueByIdentifier(identifier)!!
        valueByIdentifier.applyChangesJson(updatesJson)
    }

    override fun getAllElements(): List<T> {
        return this.list.map { it.clusterListItem }
    }

    fun setAllElements(elements: List<T>) {
        this.list.clear()
        elements.forEach { this.list.add(WrappedListItem(it)) }
    }

    override fun getArrayClass(): Class<Array<Any>> {
        return this.arrayClass
    }

    override fun addListener(listener: IClusterListListenerAdapter<T>) {
        this.listeners.add(listener)
    }

    override fun removeListener(listener: IClusterListListenerAdapter<T>) {
        this.listeners.remove(listener)
    }
}