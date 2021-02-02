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
import eu.thesimplecloud.clientserverapi.cluster.list.adapter.IClusterListAdapter
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIOAddElementToClusterList
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIORemoveElementFromClusterList
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIOUpdateClusterListElement
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
    private val adapters = CopyOnWriteArrayList<IClusterListAdapter<T>>()

    override fun addElement(item: T, fromPacket: Boolean) {
        list.removeIf { it.clusterListItem.getIdentifier() == item.getIdentifier() }
        list.add(WrappedListItem(item))

        this.adapters.forEach { it.onElementAdded(item) }
        if (!fromPacket) {
            cluster.sendPacketToAllNodes(PacketIOAddElementToClusterList(name, item)).syncUninterruptibly()
        }
    }

    override fun removeElement(identifier: Any, fromPacket: Boolean) {
        val value = getValueByIdentifier(identifier) ?: return
        list.remove(value)

        this.adapters.forEach { it.onElementRemoved(value.clusterListItem) }
        if (!fromPacket) {
            cluster.sendPacketToAllNodes(PacketIORemoveElementFromClusterList(name, identifier)).syncUninterruptibly()
        }
    }

    override fun updateElement(cachedValue: T) {
        val identifier = cachedValue.getIdentifier()
        val wrappedListItem = getValueByIdentifier(identifier)
        if (wrappedListItem == null || wrappedListItem.clusterListItem !== cachedValue)
            throw IllegalArgumentException("Cannot update a value that is not contained in the list")
        val oldValue = wrappedListItem.currentJson.getObject(cachedValue::class.java)

        this.adapters.forEach { it.onElementUpdated(oldValue, cachedValue) }

        val json = wrappedListItem.updateToItem(cachedValue)
        cluster.sendPacketToAllNodes(PacketIOUpdateClusterListElement(name, json)).syncUninterruptibly()
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

    override fun setAllElements(elements: List<T>) {
        this.list.clear()
        elements.forEach { this.list.add(WrappedListItem(it)) }
    }

    override fun getArrayClass(): Class<Array<Any>> {
        return this.arrayClass
    }

    override fun addAdapter(adapter: IClusterListAdapter<T>) {
        this.adapters.add(adapter)
    }

    override fun removeAdapter(adapter: IClusterListAdapter<T>) {
        this.adapters.remove(adapter)
    }
}