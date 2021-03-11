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

package eu.thesimplecloud.clientserverapi.cluster.packets.component

import eu.thesimplecloud.clientserverapi.cluster.component.IRemoteClusterComponent
import eu.thesimplecloud.clientserverapi.cluster.component.node.INode
import eu.thesimplecloud.clientserverapi.cluster.type.AbstractCluster
import eu.thesimplecloud.clientserverapi.lib.packet.packetsender.IPacketSender
import eu.thesimplecloud.clientserverapi.lib.packet.packettype.ObjectPacket
import eu.thesimplecloud.clientserverapi.lib.promise.ICommunicationPromise
import java.util.*
import kotlin.NoSuchElementException

class PacketIOClusterComponentLeave() : ObjectPacket<UUID>() {

    constructor(componentId: UUID) : this() {
        this.value = componentId
    }

    override suspend fun handle(sender: IPacketSender): ICommunicationPromise<Any> {
        val value = this.value ?: return contentException("value")
        val cluster = sender.getCommunicationBootstrap().getCluster()!!
        cluster as AbstractCluster
        val componentByUniqueId = cluster.getComponentManager().getComponentByUniqueId(value)
            ?: return failure(NoSuchElementException("Component not found"))

        componentByUniqueId as IRemoteClusterComponent
        val senderComponent = cluster.getComponentManager().getComponentByPacketSender(sender)
        cluster.onComponentLeave(componentByUniqueId, senderComponent as INode)
        return unit()
    }
}