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

import eu.thesimplecloud.clientserverapi.cluster.ICluster
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIOAddElementToClusterList
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIORemoveElementFromClusterList
import eu.thesimplecloud.clientserverapi.cluster.packets.clusterlist.PacketIOUpdateElementFromClusterList
import eu.thesimplecloud.clientserverapi.lib.promise.CommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import eu.thesimplecloud.clientserverapi.lib.util.Identifiable

/**
 * Created by IntelliJ IDEA.
 * Date: 31/01/2021
 * Time: 20:56
 * @author Frederick Baier
 */
class ClusterSyncList<T : Identifiable>(
    private val cluster: ICluster,
    private val name: String
) : AbstractSyncList<T>() {

    override fun addElement(element: T, fromPacket: Boolean): ICommunicationPromise<Unit> {
        super.addElement(element, fromPacket)
        if (!fromPacket) {
            return cluster.sendPacketToAllNodes(PacketIOAddElementToClusterList(name, element))
        }
        return CommunicationPromise.UNIT_PROMISE
    }

    override fun removeElement(identifier: Any, fromPacket: Boolean): ICommunicationPromise<Unit> {
        super.removeElement(identifier, fromPacket)
        if (!fromPacket) {
            return cluster.sendPacketToAllNodes(PacketIORemoveElementFromClusterList(name, identifier))
        }
        return CommunicationPromise.UNIT_PROMISE
    }

    override fun updateElement(cachedValue: T): ICommunicationPromise<Unit> {
        val changesJson = updateElement0(cachedValue)
        return cluster.sendPacketToAllNodes(PacketIOUpdateElementFromClusterList(name, changesJson))
    }
}